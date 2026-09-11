package com.khodier.hotelexplorer.features.booking

import com.khodier.hotelexplorer.core.domain.model.BookingConfirmation
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.PriceBreakdown
import java.time.LocalDate

data class BookingUiState(
    val hotel: Hotel? = null,
    val checkInDate: LocalDate? = null,
    val checkOutDate: LocalDate? = null,
    val roomsCount: Int = 1,
    val priceBreakdown: PriceBreakdown? = null,
    val dateError: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isConfirming: Boolean = false,
    val confirmation: BookingConfirmation? = null
)