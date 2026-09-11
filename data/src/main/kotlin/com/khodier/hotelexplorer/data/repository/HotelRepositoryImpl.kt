package com.khodier.hotelexplorer.data.repository

import com.khodier.hotelexplorer.core.database.dao.FavoriteDao
import com.khodier.hotelexplorer.core.database.dao.HotelDao
import com.khodier.hotelexplorer.core.domain.model.FilterOptions
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.model.PagedResult
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.core.domain.result.DataError
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.network.api.HotelApiService
import com.khodier.hotelexplorer.data.mapper.toDomain
import com.khodier.hotelexplorer.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class HotelRepositoryImpl @Inject constructor(
    private val hotelApiService: HotelApiService,
    private val hotelDao: HotelDao,
    private val favoriteDao: FavoriteDao
) : HotelRepository {

    override fun getHotels(page: Int, filters: HotelFilters): Flow<DataResult<PagedResult<Hotel>>> {
        return combine(
            hotelDao.getAllHotels(),
            favoriteDao.getFavoriteIds()
        ) { entities, favoriteIds ->
            val domainHotels = entities.map { entity ->
                entity.toDomain().copy(isFavorite = entity.id in favoriteIds)
            }
            val filtered = applyFilters(domainHotels, filters)
            val paged = applyPagination(filtered, page, pageSize = 10)
            DataResult.Success(paged) as DataResult<PagedResult<Hotel>>
        }
    }

    override fun getHotelById(id: Long): Flow<DataResult<Hotel>> {
        return combine(
            hotelDao.getHotelById(id),
            favoriteDao.isFavorite(id)
        ) { entity, isFavorite ->
            if (entity != null) {
                DataResult.Success(entity.toDomain().copy(isFavorite = isFavorite))
            } else {
                DataResult.Error(DataError.CacheEmpty)
            }
        }
    }

    override suspend fun refreshHotels(): DataResult<Unit> {
        return try {
            val response = hotelApiService.getHotels()
            val entities = response.hotels.map { it.toEntity() }
            hotelDao.insertHotels(entities)
            DataResult.Success(Unit)
        } catch (e: IOException) {
            DataResult.Error(DataError.NoInternet)
        }
    }

    override fun getFilterOptions(): Flow<DataResult<FilterOptions>> {
        return hotelDao.getAllHotels().map { entities ->
            val availableCities = entities.map { it.city }.distinct().sorted()
            val prices = entities.map { it.pricePerNight }
            val minPrice = prices.minOrNull() ?: 0.0
            val maxPrice = prices.maxOrNull() ?: 1000.0
            DataResult.Success(
                FilterOptions(
                    cities = availableCities,
                    minPrice = minPrice,
                    maxPrice = maxPrice
                )
            )
        }
    }

    private fun applyFilters(hotels: List<Hotel>, filters: HotelFilters): List<Hotel> {
        val searchQuery = filters.searchQuery
        val city = filters.city
        val minRating = filters.minRating
        val minPrice = filters.minPrice
        val maxPrice = filters.maxPrice

        return hotels.filter { hotel ->
            (searchQuery == null || hotel.name.contains(searchQuery, ignoreCase = true)) &&
                    (city == null || hotel.city == city) &&
                    (minRating == null || hotel.rating >= minRating) &&
                    (minPrice == null || hotel.pricePerNight >= minPrice) &&
                    (maxPrice == null || hotel.pricePerNight <= maxPrice)
        }
    }

    private fun applyPagination(hotels: List<Hotel>, page: Int, pageSize: Int): PagedResult<Hotel> {
        val fromIndex = (page - 1) * pageSize
        val toIndex = minOf(fromIndex + pageSize, hotels.size)
        val pageItems =
            if (fromIndex >= hotels.size) emptyList() else hotels.subList(fromIndex, toIndex)
        val hasMore = toIndex < hotels.size
        return PagedResult(items = pageItems, currentPage = page, hasMore = hasMore)
    }
}
