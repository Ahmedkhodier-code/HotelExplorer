package com.khodier.hotelexplorer.core.domain.model

data class PriceBreakdown(
    val nights: Int,
    val roomsCount: Int,
    val basePrice: Double,
    val vatAmount: Double,
    val totalPrice: Double
)
