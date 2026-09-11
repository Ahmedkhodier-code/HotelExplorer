package com.khodier.hotelexplorer.features.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.result.DataError
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetFilterOptionsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.RefreshHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val refreshHotelsUseCase: RefreshHotelsUseCase,
    private val getFilterOptionsUseCase: GetFilterOptionsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers
) : ViewModel() {

    private val _uiState = MutableStateFlow(HotelsUiState())
    val uiState: StateFlow<HotelsUiState> = _uiState.asStateFlow()

    private val searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch(dispatchers.io) {
            refreshHotelsUseCase()
            loadPage(page = 1, isNewSearch = true)
        }
        observeSearchQuery()
        observeFilterOptions()
        loadPage(page = 1, isNewSearch = true)
    }

    private fun observeFilterOptions() {
        viewModelScope.launch(dispatchers.io) {
            getFilterOptionsUseCase().collect { result ->
                if (result is DataResult.Success) {
                    val options = result.data
                    _uiState.update { state ->
                        state.copy(
                            availableCities = options.cities,
                            minAvailablePrice = options.minPrice,
                            maxAvailablePrice = options.maxPrice
                        )
                    }
                }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .drop(1)
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update { it.copy(searchQuery = query) }
                    loadPage(page = 1, isNewSearch = true)
                }
        }
    }

    private fun loadPage(page: Int, isNewSearch: Boolean) {
        viewModelScope.launch(dispatchers.io) {
            if (page == 1) {
                _uiState.update { it.copy(isLoading = true, error = null) }
            } else {
                _uiState.update { it.copy(isLoadingNextPage = true) }
            }

            val currentFilters = _uiState.value.filters.copy(
                searchQuery = _uiState.value.searchQuery.ifBlank { null }
            )

            when (val result = getHotelsUseCase(page, currentFilters).first()) {

                is DataResult.Success -> {
                    val pagedResult = result.data
                    _uiState.update { state ->
                        val updatedHotels = if (isNewSearch) {
                            pagedResult.items
                        } else {
                            state.hotels + pagedResult.items
                        }
                        state.copy(
                            hotels = updatedHotels,
                            currentPage = pagedResult.currentPage,
                            hasMorePages = pagedResult.hasMore,
                            isLoading = false,
                            isLoadingNextPage = false
                        )
                    }
                }

                is DataResult.Error -> {
                    val errorMessage = when (val error = result.error) {
                        is DataError.NoInternet -> "No internet connection"
                        is DataError.Timeout -> "Request timed out"
                        is DataError.ServerError -> "Server error (${error.code})"
                        is DataError.CacheEmpty -> "No cached data available"
                        is DataError.Unknown -> error.message ?: "Unknown error occurred"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoadingNextPage = false,
                            error = errorMessage
                        )
                    }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    fun onFilterChanged(filters: HotelFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadPage(page = 1, isNewSearch = true)
    }


    fun onLoadNextPage() {
        if (!_uiState.value.isLoadingNextPage && _uiState.value.hasMorePages) {
            loadPage(page = _uiState.value.currentPage + 1, isNewSearch = false)
        }
    }

    fun onToggleFavorite(hotelId: Long) {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { state ->
                val updatedHotels = state.hotels.map { hotel ->
                    if (hotel.id == hotelId) hotel.copy(isFavorite = !hotel.isFavorite) else hotel
                }
                state.copy(hotels = updatedHotels)
            }
            toggleFavoriteUseCase(hotelId)
        }
    }
}