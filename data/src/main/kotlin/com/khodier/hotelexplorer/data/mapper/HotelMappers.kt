package com.khodier.hotelexplorer.data.mapper

import com.khodier.hotelexplorer.core.database.entity.HotelEntity
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.network.dto.HotelDto


fun HotelDto.toEntity(): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        city = city,
        rating = rating,
        pricePerNight = pricePerNight,
        description = description,
        address = address,
        latitude = latitude,
        longitude = longitude,
        images = images,
        amenities = amenities
    )
}

fun HotelEntity.toDomain(): Hotel {
    return Hotel(
        id = id,
        name = name,
        city = city,
        rating = rating,
        pricePerNight = pricePerNight,
        description = description,
        address = address,
        location = HotelLocation(latitude = latitude, longitude = longitude),
        images = images,
        amenities = amenities
    )
}