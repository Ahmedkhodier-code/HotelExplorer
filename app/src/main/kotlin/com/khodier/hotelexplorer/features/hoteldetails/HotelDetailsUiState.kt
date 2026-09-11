package com.khodier.hotelexplorer.features.hoteldetails

import com.khodier.hotelexplorer.core.domain.model.Hotel

data class HotelDetailsUiState(
    val hotel: Hotel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)