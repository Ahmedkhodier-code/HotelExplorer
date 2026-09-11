package com.khodier.hotelexplorer.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.khodier.hotelexplorer.core.database.converters.HotelTypeConverter
import com.khodier.hotelexplorer.core.database.dao.FavoriteDao
import com.khodier.hotelexplorer.core.database.dao.HotelDao
import com.khodier.hotelexplorer.core.database.entity.FavoriteEntity
import com.khodier.hotelexplorer.core.database.entity.HotelEntity


@Database(
    entities = [
        HotelEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(HotelTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        const val DATABASE_NAME = "hotel_explorer_db"
    }
}