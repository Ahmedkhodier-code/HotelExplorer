package com.khodier.hotelexplorer.data.di

import com.khodier.hotelexplorer.core.domain.repository.FavoritesRepository
import com.khodier.hotelexplorer.core.domain.repository.HotelRepository
import com.khodier.hotelexplorer.core.domain.usecase.booking.CalculateBookingPriceUseCase
import com.khodier.hotelexplorer.core.domain.usecase.booking.ConfirmBookingUseCase
import com.khodier.hotelexplorer.core.domain.usecase.booking.ValidateBookingDatesUseCase
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ObserveFavoritesUseCase
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetFilterOptionsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelDetailsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.RefreshHotelsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideGetHotelsUseCase(hotelRepository: HotelRepository): GetHotelsUseCase =
        GetHotelsUseCase(hotelRepository)

    @Provides
    fun provideRefreshHotelsUseCase(hotelRepository: HotelRepository): RefreshHotelsUseCase =
        RefreshHotelsUseCase(hotelRepository)

    @Provides
    fun provideGetFilterOptionsUseCase(hotelRepository: HotelRepository): GetFilterOptionsUseCase =
        GetFilterOptionsUseCase(hotelRepository)

    @Provides
    fun provideGetHotelDetailsUseCase(hotelRepository: HotelRepository): GetHotelDetailsUseCase =
        GetHotelDetailsUseCase(hotelRepository)

    @Provides
    fun provideObserveFavoritesUseCase(favoritesRepository: FavoritesRepository): ObserveFavoritesUseCase =
        ObserveFavoritesUseCase(favoritesRepository)

    @Provides
    fun provideToggleFavoriteUseCase(favoritesRepository: FavoritesRepository): ToggleFavoriteUseCase =
        ToggleFavoriteUseCase(favoritesRepository)

    @Provides
    fun provideValidateBookingDatesUseCase(): ValidateBookingDatesUseCase =
        ValidateBookingDatesUseCase()

    @Provides
    fun provideCalculateBookingPriceUseCase(): CalculateBookingPriceUseCase =
        CalculateBookingPriceUseCase()

    @Provides
    fun provideConfirmBookingUseCase(
        validateBookingDatesUseCase: ValidateBookingDatesUseCase,
        calculateBookingPriceUseCase: CalculateBookingPriceUseCase
    ): ConfirmBookingUseCase =
        ConfirmBookingUseCase(validateBookingDatesUseCase, calculateBookingPriceUseCase)
}