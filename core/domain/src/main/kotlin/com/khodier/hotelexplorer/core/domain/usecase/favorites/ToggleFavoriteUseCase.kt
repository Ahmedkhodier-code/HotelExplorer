package com.khodier.hotelexplorer.core.domain.usecase.favorites

import com.khodier.hotelexplorer.core.domain.repository.FavoritesRepository

class ToggleFavoriteUseCase(private val favoritesRepository: FavoritesRepository) {
    suspend operator fun invoke(hotelId: Long) =
        favoritesRepository.toggleFavorite(hotelId = hotelId)
}