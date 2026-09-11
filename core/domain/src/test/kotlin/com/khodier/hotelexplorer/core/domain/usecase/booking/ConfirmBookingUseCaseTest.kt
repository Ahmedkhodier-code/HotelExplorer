package com.khodier.hotelexplorer.core.domain.usecase.booking

import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.domain.model.Booking
import org.junit.Test
import java.time.LocalDate

class ConfirmBookingUseCaseTest {
    val calculateBookingPriceUseCase = CalculateBookingPriceUseCase()
    private val validateBookingDatesUseCase = ValidateBookingDatesUseCase()

    val useCase = ConfirmBookingUseCase(
        calculateBookingPriceUseCase = calculateBookingPriceUseCase,
        validateBookingDatesUseCase = validateBookingDatesUseCase,
        generateBookingReference = { "HTL-450E6CB1" }
    )

    private val today = LocalDate.now()


    @Test
    fun `test confirm booking success`() {
        val booking = Booking(
            hotelId = 1,
            checkInDate = today,
            checkOutDate = today.plusDays(5),
            roomsCount = 2
        )
        val pricePerNight = 100.0
        val result = useCase(booking, pricePerNight)
        assertThat(result).isInstanceOf(ConfirmBookingResult.Success::class.java)
        val successResult = result as ConfirmBookingResult.Success
        assertThat(successResult.confirmation.bookingReference).isEqualTo("HTL-450E6CB1")
    }

    @Test
    fun `test confirm booking invalid dates & check-in date in the past`() {
        val booking = Booking(
            hotelId = 1,
            checkInDate = today.minusDays(1),
            checkOutDate = today,
            roomsCount = 2
        )
        val pricePerNight = 100.0
        val result = useCase(booking, pricePerNight)
        assertThat(result).isInstanceOf(ConfirmBookingResult.InvalidDates::class.java)
        val invalidDatesResult = result as ConfirmBookingResult.InvalidDates
        assertThat(invalidDatesResult.reason).isInstanceOf(BookingDatesValidationResult.CheckInInPast::class.java)
    }

    @Test
    fun `test confirm booking invalid dates & check-out date is equal to check-in date`() {
        val booking = Booking(
            hotelId = 1,
            checkInDate = today,
            checkOutDate = today,
            roomsCount = 2
        )
        val pricePerNight = 100.0
        val result = useCase(booking, pricePerNight)
        assertThat(result).isInstanceOf(ConfirmBookingResult.InvalidDates::class.java)
        val invalidDatesResult = result as ConfirmBookingResult.InvalidDates
        assertThat(invalidDatesResult.reason).isInstanceOf(BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn::class.java)
    }

    @Test
    fun `test confirm booking invalid dates & check-out date is before check-in date`() {
        val booking = Booking(
            hotelId = 1,
            checkInDate = today,
            checkOutDate = today.minusDays(1),
            roomsCount = 2
        )
        val pricePerNight = 100.0
        val result = useCase(booking, pricePerNight)
        assertThat(result).isInstanceOf(ConfirmBookingResult.InvalidDates::class.java)
        val invalidDatesResult = result as ConfirmBookingResult.InvalidDates
        assertThat(invalidDatesResult.reason).isInstanceOf(BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn::class.java)
    }
}