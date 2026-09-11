package com.khodier.hotelexplorer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.khodier.hotelexplorer.core.database.entity.HotelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HotelDao {
    @Query("SELECT * FROM hotels")
    fun getAllHotels(): Flow<List<HotelEntity>>

    @Query("SELECT * FROM hotels WHERE id = :id")
    fun getHotelById(id: Long): Flow<HotelEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotels(hotels: List<HotelEntity>)

    @Query("SELECT COUNT(*) FROM hotels")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHotel(hotel: HotelEntity)
}