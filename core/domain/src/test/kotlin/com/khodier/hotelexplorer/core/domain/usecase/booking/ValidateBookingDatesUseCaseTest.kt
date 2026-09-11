package com.khodier.hotelexplorer.core.domain.usecase.booking

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class ValidateBookingDatesUseCaseTest {
    private val validateBookingDatesUseCase = ValidateBookingDatesUseCase()
    private val today = LocalDate.now()

    @Test
    fun `check-in date in the past is invalid`() {
        val checkIn = today.minusDays(1)
        val checkOut = today.plusDays(5)
        val result = validateBookingDatesUseCase(checkIn, checkOut, today)
        assertThat(result).isInstanceOf(BookingDatesValidationResult.CheckInInPast::class.java)
    }

    @Test
    fun `check-out date is before check-in date is invalid`() {
        val checkIn = today.plusDays(2)
        val checkOut = today.plusDays(1)
        val result = validateBookingDatesUseCase(checkIn, checkOut, today)
        assertThat(result).isInstanceOf(BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn::class.java)
    }

    @Test
    fun `check-out date is equal to check-in date is invalid`() {
        val checkIn = today.plusDays(2)
        val checkOut = today.plusDays(2)
        val result = validateBookingDatesUseCase(checkIn, checkOut, today)
        assertThat(result).isInstanceOf(BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn::class.java)
    }

    @Test
    fun `valid check-in and check-out dates are valid and return valid result`() {
        val checkIn = today
        val checkOut = today.plusDays(5)
        val result = validateBookingDatesUseCase(checkIn, checkOut, today)
        assertThat(result).isInstanceOf(BookingDatesValidationResult.Valid::class.java)
    }
}