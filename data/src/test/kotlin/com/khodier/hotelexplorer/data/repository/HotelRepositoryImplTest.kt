package com.khodier.hotelexplorer.data.repository

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.database.dao.FavoriteDao
import com.khodier.hotelexplorer.core.database.dao.HotelDao
import com.khodier.hotelexplorer.core.database.entity.HotelEntity
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.network.api.HotelApiService
import com.khodier.hotelexplorer.core.network.dto.HotelDto
import com.khodier.hotelexplorer.core.network.dto.HotelsResponseDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class HotelRepositoryImplTest {

    private val apiService: HotelApiService = mockk()
    private val hotelDao: HotelDao = mockk()
    private val favoriteDao: FavoriteDao = mockk()

    private lateinit var repository: HotelRepositoryImpl

    @Before
    fun setUp() {
        repository = HotelRepositoryImpl(apiService, hotelDao, favoriteDao)
    }

    @Test
    fun `getHotels emits cached data from database`() = runTest {
        val mockEntities = listOf(
            HotelEntity(
                id = 1L,
                name = "Hotel 1",
                city = "Cairo",
                rating = 4.5,
                pricePerNight = 100.0,
                description = "Desc",
                address = "Address",
                latitude = 0.0,
                longitude = 0.0,
                images = emptyList(),
                amenities = emptyList()
            )
        )
        coEvery { hotelDao.getAllHotels() } returns flowOf(mockEntities)
        coEvery { favoriteDao.getFavoriteIds() } returns flowOf(emptyList())

        repository.getHotels(1, HotelFilters()).test {
            val result = awaitItem()
            assertThat(result is DataResult.Success).isTrue()
            val hotels = (result as DataResult.Success).data.items
            assertThat(hotels).hasSize(1)
            assertThat(hotels[0].id).isEqualTo(1L)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `refreshHotels fetches from api and saves to database`() = runTest {
        val mockDtos = listOf(
            HotelDto(
                id = 1L,
                name = "Hotel 1",
                city = "Cairo",
                rating = 4.5,
                pricePerNight = 100.0,
                description = "Desc",
                address = "Address",
                latitude = 0.0,
                longitude = 0.0,
                images = emptyList(),
                amenities = emptyList()
            )
        )
        val mockResponse = HotelsResponseDto(mockDtos)

        coEvery { apiService.getHotels() } returns mockResponse
        coEvery { favoriteDao.getFavoriteIds() } returns flowOf(emptyList())
        coEvery { hotelDao.insertHotels(any()) } returns Unit

        val result = repository.refreshHotels()

        assertThat(result is DataResult.Success).isTrue()
        coVerify { hotelDao.insertHotels(any()) }
    }
}
