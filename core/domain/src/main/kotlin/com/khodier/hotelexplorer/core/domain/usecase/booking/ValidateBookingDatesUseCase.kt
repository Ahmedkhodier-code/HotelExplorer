package com.khodier.hotelexplorer.core.domain.usecase.booking

import java.time.LocalDate

sealed class BookingDatesValidationResult {
    data object Valid : BookingDatesValidationResult()
    data object CheckInInPast : BookingDatesValidationResult()
    data object CheckOutBeforeOrEqualCheckIn : BookingDatesValidationResult()
}

class ValidateBookingDatesUseCase {
    operator fun invoke(
        checkIn: LocalDate,
        checkOut: LocalDate,
        today: LocalDate = LocalDate.now()
    ): BookingDatesValidationResult = when {
        checkIn < today -> BookingDatesValidationResult.CheckInInPast
        checkOut <= checkIn -> BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn
        else -> BookingDatesValidationResult.Valid
    }
}
