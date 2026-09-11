package com.khodier.hotelexplorer.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hotels")
data class HotelEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val city: String,
    val rating: Double,
    val pricePerNight: Double,
    val description: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val images: List<String>,
    val amenities: List<String>
)