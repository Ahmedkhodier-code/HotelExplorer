package com.khodier.hotelexplorer.core.domain.model

data class BookingConfirmation(
    val bookingReference: String,
    val booking: Booking,
    val priceBreakdown: PriceBreakdown
)
