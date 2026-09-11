package com.khodier.hotelexplorer.core.domain.usecase.booking

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class CalculateBookingPriceUseCaseTest {
    val calculateBookingPriceUseCase = CalculateBookingPriceUseCase()
    private val today = LocalDate.now()

    @Test
    fun `calculate booking price with 1 night`() {
        val checkIn = today
        val checkOut = checkIn.plusDays(1)
        val roomsCount = 2
        val pricePerNight = 100.0
        val result = calculateBookingPriceUseCase(checkIn, checkOut, roomsCount, pricePerNight)
        assertThat(result.nights).isEqualTo(1)
        assertThat(result.roomsCount).isEqualTo(2)
        assertThat(result.basePrice).isEqualTo(200.0)
        assertThat(result.vatAmount).isEqualTo(30.0)
        assertThat(result.totalPrice).isEqualTo(230.0)
    }


    @Test
    fun `calculate booking price with more than 1 night`() {
        val checkIn = today
        val checkOut = checkIn.plusDays(5)
        val roomsCount = 2
        val pricePerNight = 100.0
        val result = calculateBookingPriceUseCase(checkIn, checkOut, roomsCount, pricePerNight)
        assertThat(result.nights).isEqualTo(5)
        assertThat(result.roomsCount).isEqualTo(2)
        assertThat(result.basePrice).isEqualTo(1000.0)
        assertThat(result.vatAmount).isEqualTo(150.0)
        assertThat(result.totalPrice).isEqualTo(1150.0)
    }

    @Test
    fun `vat is calculated from base price not total`(){
        val checkIn = today
        val checkOut = checkIn.plusDays(5)
        val roomsCount = 2
        val pricePerNight = 100.0
        val result = calculateBookingPriceUseCase(checkIn, checkOut, roomsCount, pricePerNight)
        assertThat(result.vatAmount).isEqualTo(result.basePrice * VAT_RATE)
    }

    @Test
    fun `calculate booking price with month difference`(){
        val checkIn = LocalDate.of(2026, 1, 30)
        val checkOut = checkIn.plusDays(5)
        val roomsCount = 2
        val pricePerNight = 100.0
        val result = calculateBookingPriceUseCase(checkIn, checkOut, roomsCount, pricePerNight)
        assertThat(result.nights).isEqualTo(5)
        assertThat(result.roomsCount).isEqualTo(2)
    }
}