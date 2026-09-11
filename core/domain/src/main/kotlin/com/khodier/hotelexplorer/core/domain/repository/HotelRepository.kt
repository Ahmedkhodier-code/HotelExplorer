package com.khodier.hotelexplorer.core.domain.repository

import com.khodier.hotelexplorer.core.domain.model.FilterOptions
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.model.PagedResult
import com.khodier.hotelexplorer.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    fun getHotels(page: Int, filters: HotelFilters): Flow<DataResult<PagedResult<Hotel>>>
    fun getHotelById(id: Long): Flow<DataResult<Hotel>>
    suspend fun refreshHotels(): DataResult<Unit>

    fun getFilterOptions(): Flow<DataResult<FilterOptions>>
}