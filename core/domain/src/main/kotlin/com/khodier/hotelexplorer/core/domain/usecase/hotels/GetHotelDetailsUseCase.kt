package com.khodier.hotelexplorer.core.domain.usecase.hotels

import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

class GetHotelDetailsUseCase(
    private val hotelRepository: HotelRepository
) {
    operator fun invoke(hotelId: Long): Flow<DataResult<Hotel>> =
        hotelRepository.getHotelById(id = hotelId)
}