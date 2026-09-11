package com.khodier.hotelexplorer.core.network.api

import com.khodier.hotelexplorer.core.network.dto.HotelsResponseDto
import retrofit2.http.GET

interface HotelApiService {
    @GET(".json")
    suspend fun getHotels(): HotelsResponseDto
}
