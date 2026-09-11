package com.khodier.hotelexplorer.core.database.di

import android.content.Context
import androidx.room.Room
import com.khodier.hotelexplorer.core.database.AppDatabase
import com.khodier.hotelexplorer.core.database.dao.FavoriteDao
import com.khodier.hotelexplorer.core.database.dao.HotelDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideHotelDao(db: AppDatabase): HotelDao = db.hotelDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()

}