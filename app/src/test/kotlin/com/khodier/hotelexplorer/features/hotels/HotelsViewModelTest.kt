package com.khodier.hotelexplorer.features.hotels

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.domain.model.PagedResult
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetFilterOptionsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.RefreshHotelsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HotelsViewModelTest {

    private val getHotelsUseCase: GetHotelsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val refreshHotelsUseCase: RefreshHotelsUseCase = mockk(relaxed = true)
    private val getFilterOptionsUseCase: GetFilterOptionsUseCase = mockk(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : CoroutineDispatchers {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private lateinit var viewModel: HotelsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then loads hotels successfully and updates uiState`() = runTest {
        val mockHotels = listOf(
            Hotel(
                id = 1,
                name = "The Grand Luminary",
                city = "Cairo",
                pricePerNight = 3200.0,
                rating = 4.9,
                images = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945"),
                amenities = listOf("Free WiFi", "Swimming Pool", "Restaurant"),
                isFavorite = false,
                description = "Luxury hotel overlooking the Nile.",
                address = "Corniche El Nile, Cairo, Egypt",
                location = HotelLocation(30.0444, 31.2357)
            )
        )
        val pagedResult = PagedResult(items = mockHotels, currentPage = 1, hasMore = false)

        coEvery { getHotelsUseCase(any(), any()) } returns flowOf(DataResult.Success(pagedResult))

        viewModel = HotelsViewModel(
            getHotelsUseCase,
            refreshHotelsUseCase,
            getFilterOptionsUseCase,
            toggleFavoriteUseCase,
            testDispatchers
        )

        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.hotels).isEqualTo(mockHotels)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }
}