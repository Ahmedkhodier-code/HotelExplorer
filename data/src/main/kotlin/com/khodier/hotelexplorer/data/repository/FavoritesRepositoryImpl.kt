package com.khodier.hotelexplorer.data.repository

import com.khodier.hotelexplorer.core.database.dao.FavoriteDao
import com.khodier.hotelexplorer.core.database.dao.HotelDao
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.repository.FavoritesRepository
import com.khodier.hotelexplorer.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val hotelDao: HotelDao,
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {

    override fun observeFavorites(): Flow<List<Hotel>> {
        return combine(
            hotelDao.getAllHotels(),
            favoriteDao.getFavoriteIds()
        ) { entities, favoriteIds ->
            entities
                .filter { it.id in favoriteIds }
                .map { it.toDomain().copy(isFavorite = true) }
        }
    }

    override suspend fun toggleFavorite(hotelId: Long) {
        favoriteDao.toggleFavorite(hotelId)
    }

    override fun isFavorite(hotelId: Long): Flow<Boolean> {
        return favoriteDao.isFavorite(hotelId)
    }
}