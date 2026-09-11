package com.khodier.hotelexplorer.core.domain.repository

import com.khodier.hotelexplorer.core.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository{
    fun observeFavorites(): Flow<List<Hotel>>
    suspend fun toggleFavorite(hotelId: Long)
    fun isFavorite(hotelId: Long): Flow<Boolean>
}