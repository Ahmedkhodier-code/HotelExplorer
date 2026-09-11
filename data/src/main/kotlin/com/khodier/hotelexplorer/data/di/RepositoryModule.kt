package com.khodier.hotelexplorer.data.di

import com.khodier.hotelexplorer.core.domain.repository.FavoritesRepository
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.data.repository.FavoritesRepositoryImpl
import com.khodier.hotelexplorer.data.repository.HotelRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindHotelRepository(impl: HotelRepositoryImpl): HotelRepository

    @Binds
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository
}