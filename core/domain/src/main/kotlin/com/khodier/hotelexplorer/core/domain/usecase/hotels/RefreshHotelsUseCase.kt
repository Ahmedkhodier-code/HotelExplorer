package com.khodier.hotelexplorer.core.domain.usecase.hotels

import com.khodier.hotelexplorer.core.domain.repository.HotelRepository


class RefreshHotelsUseCase(
    private val hotelRepository: HotelRepository
) {
   suspend operator fun invoke() {
        hotelRepository.refreshHotels()
    }
}