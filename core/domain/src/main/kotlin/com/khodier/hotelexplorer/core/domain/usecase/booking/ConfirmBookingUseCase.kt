package com.khodier.hotelexplorer.core.domain.usecase.booking

import com.khodier.hotelexplorer.core.domain.model.Booking
import com.khodier.hotelexplorer.core.domain.model.BookingConfirmation
import java.util.UUID

sealed class ConfirmBookingResult {
    data class Success(val confirmation: BookingConfirmation) : ConfirmBookingResult()
    data class InvalidDates(val reason: BookingDatesValidationResult) : ConfirmBookingResult()
}

class ConfirmBookingUseCase(
    private val validateBookingDatesUseCase: ValidateBookingDatesUseCase,
    private val calculateBookingPriceUseCase: CalculateBookingPriceUseCase,
    private val generateBookingReference: () -> String = {
        "HTL-" + UUID.randomUUID().toString().take(8).uppercase()
    }

) {
    operator fun invoke(booking: Booking, pricePerNight: Double): ConfirmBookingResult =
        when (val bookingDatesValidationResult = validateBookingDatesUseCase(
            checkIn = booking.checkInDate,
            checkOut = booking.checkOutDate
        )) {
            BookingDatesValidationResult.Valid -> {
                val priceBreakdown = calculateBookingPriceUseCase(
                    checkIn = booking.checkInDate,
                    checkOut = booking.checkOutDate,
                    roomsCount = booking.roomsCount,
                    pricePerNight = pricePerNight
                )
                ConfirmBookingResult.Success(
                    BookingConfirmation(
                        bookingReference = generateBookingReference(),
                        booking = booking,
                        priceBreakdown = priceBreakdown
                    )
                )
            }

            BookingDatesValidationResult.CheckInInPast,
            BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn -> {
                ConfirmBookingResult.InvalidDates(bookingDatesValidationResult)
            }
        }
}