package com.khodier.hotelexplorer.core.domain.usecase.booking

import com.khodier.hotelexplorer.core.domain.model.PriceBreakdown
import java.time.LocalDate
import java.time.temporal.ChronoUnit

const val VAT_RATE = 0.15

class CalculateBookingPriceUseCase {
    operator fun invoke(checkIn: LocalDate, checkOut: LocalDate, roomsCount: Int, pricePerNight: Double): PriceBreakdown{
        val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
        val basePrice = nights * roomsCount * pricePerNight
        val vatAmount = basePrice * VAT_RATE
        val totalPrice = basePrice + vatAmount
        return PriceBreakdown(nights, roomsCount, basePrice, vatAmount, totalPrice)
    }
}