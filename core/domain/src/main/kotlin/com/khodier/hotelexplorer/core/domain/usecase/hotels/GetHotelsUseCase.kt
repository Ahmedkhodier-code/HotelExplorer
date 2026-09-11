package com.khodier.hotelexplorer.core.domain.usecase.hotels

import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.model.PagedResult
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

class GetHotelsUseCase(
    private val hotelRepository: HotelRepository
) {
    operator fun invoke(page: Int, filters: HotelFilters): Flow<DataResult<PagedResult<Hotel>>> =
        hotelRepository.getHotels(page = page, filters = filters)
}