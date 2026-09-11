package com.khodier.hotelexplorer.core.domain.model

import java.time.LocalDate

data class Booking(
    val hotelId: Long,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val roomsCount: Int
)
