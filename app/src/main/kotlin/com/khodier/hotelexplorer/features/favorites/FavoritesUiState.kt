package com.khodier.hotelexplorer.features.favorites

import com.khodier.hotelexplorer.core.domain.model.Hotel

data class FavoritesUiState(
    val favorites: List<Hotel> = emptyList(),
    val isLoading: Boolean = true
    )