package com.khodier.hotelexplorer.core.domain.usecase.hotels

import com.khodier.hotelexplorer.core.domain.model.FilterOptions
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.core.domain.result.DataResult
import kotlinx.coroutines.flow.Flow

class GetFilterOptionsUseCase(
    private val hotelRepository: HotelRepository
) {
    operator fun invoke(): Flow<DataResult<FilterOptions>> =
        hotelRepository.getFilterOptions()
}
