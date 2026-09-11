package com.khodier.hotelexplorer.features.hotels

import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelFilters

data class HotelsUiState(
    val hotels: List<Hotel> = emptyList(),
    val minAvailablePrice: Double = 0.0,
    val maxAvailablePrice: Double = 1000.0,
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val isFromCache: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val filters: HotelFilters = HotelFilters(),
    val currentPage: Int = 1,
    val availableCities: List<String> = emptyList(),
    val hasMorePages: Boolean = true
)