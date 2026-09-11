package com.khodier.hotelexplorer.core.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class HotelDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "city") val city: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "pricePerNight") val pricePerNight: Double,
    @Json(name = "description") val description: String,
    @Json(name = "address") val address: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "images") val images: List<String>,
    @Json(name = "amenities") val amenities: List<String>
)