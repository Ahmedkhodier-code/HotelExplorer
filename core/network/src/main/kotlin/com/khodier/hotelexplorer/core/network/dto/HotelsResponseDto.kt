package com.khodier.hotelexplorer.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HotelsResponseDto(
    @Json(name = "hotels") val hotels: List<HotelDto>
)