package com.khodier.hotelexplorer.features.hoteldetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.result.DataError
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailsViewModel @Inject constructor(
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(HotelDetailsUiState())
    val uiState: StateFlow<HotelDetailsUiState> = _uiState.asStateFlow()
    private val hotelId: Long?
        get() = savedStateHandle.get<Long>("hotel_id")

    init {
        loadHotelDetails()
    }

    fun loadHotelDetails() {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            hotelId?.let {
                when (val result = getHotelDetailsUseCase(it).first()) {
                    is DataResult.Success -> {
                        val hotelResult = result.data
                        _uiState.update { state ->
                            state.copy(
                                hotel = hotelResult,
                                isLoading = false,
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
                        _uiState.update { state ->
                            state.copy(error = errorMessage)
                        }
                    }
                }
            }
        }
    }

    fun onToggleFavorite(hotelId: Long) {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { state ->
                val updatedHotel = state.hotel?.copy(isFavorite = !state.hotel.isFavorite)
                state.copy(hotel = updatedHotel)
            }
            toggleFavoriteUseCase(hotelId)
        }
    }
}
