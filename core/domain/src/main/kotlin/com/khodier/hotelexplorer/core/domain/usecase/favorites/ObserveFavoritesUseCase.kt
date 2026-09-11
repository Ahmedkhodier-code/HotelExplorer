package com.khodier.hotelexplorer.core.domain.usecase.favorites

import com.khodier.hotelexplorer.core.domain.repository.FavoritesRepository

class ObserveFavoritesUseCase(private val favoritesRepository: FavoritesRepository) {
    operator fun invoke() = favoritesRepository.observeFavorites()
}