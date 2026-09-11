package com.khodier.hotelexplorer.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.khodier.hotelexplorer.core.database.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT hotelId FROM favorites")
    fun getFavoriteIds(): Flow<List<Long>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE hotelId = :hotelId)")
    fun isFavorite(hotelId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE hotelId = :hotelId")
    suspend fun removeFavorite(hotelId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE hotelId = :hotelId)")
    suspend fun isFavoriteOnce(hotelId: Long): Boolean

    @Transaction
    suspend fun toggleFavorite(hotelId: Long) {
        if (isFavoriteOnce(hotelId)) {
            removeFavorite(hotelId)
        } else {
            addFavorite(FavoriteEntity(hotelId))
        }
    }
}