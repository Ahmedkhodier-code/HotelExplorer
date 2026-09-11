package com.khodier.hotelexplorer.core.domain.model

data class Hotel(
    val id: Long,
    val name: String,
    val city: String,
    val rating: Double,
    val pricePerNight: Double,
    val images: List<String>,
    val description: String,
    val address: String,
    val amenities: List<String>,
    val location: HotelLocation,
    val isFavorite: Boolean = false
)

data class HotelLocation(
    val latitude: Double,
    val longitude: Double
)
