package com.khodier.hotelexplorer.features.favorites

import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ObserveFavoritesUseCase
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
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
class FavoritesViewModelTest {

    private val observeFavoritesUseCase: ObserveFavoritesUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : CoroutineDispatchers {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private lateinit var viewModel: FavoritesViewModel

    private val mockHotels = listOf(
        Hotel(
            id = 1L,
            name = "Grand Hotel",
            city = "Paris",
            rating = 4.8,
            pricePerNight = 250.0,
            images = listOf("https://image.url"),
            description = "A wonderful hotel",
            address = "123 Champs Elysees",
            amenities = listOf("WiFi", "Pool"),
            location = HotelLocation(48.8566, 2.3522),
            isFavorite = true
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when init then observes favorites successfully`() = runTest {
        coEvery { observeFavoritesUseCase() } returns flowOf(mockHotels)

        viewModel = FavoritesViewModel(
            observeFavoritesUseCase = observeFavoritesUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            dispatchers = testDispatchers
        )

        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.favorites).isEqualTo(mockHotels)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `when onToggleFavorite then calls usecase`() = runTest {
        coEvery { observeFavoritesUseCase() } returns flowOf(mockHotels)

        viewModel = FavoritesViewModel(
            observeFavoritesUseCase = observeFavoritesUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            dispatchers = testDispatchers
        )

        viewModel.onToggleFavorite(1L)
        testScheduler.advanceUntilIdle()

        coVerify(exactly = 1) { toggleFavoriteUseCase(1L) }
    }
}
