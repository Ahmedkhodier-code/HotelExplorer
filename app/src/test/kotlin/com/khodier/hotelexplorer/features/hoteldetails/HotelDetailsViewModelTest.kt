package com.khodier.hotelexplorer.features.hoteldetails

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.domain.result.DataError
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.favorites.ToggleFavoriteUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelDetailsUseCase
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
class HotelDetailsViewModelTest {

    private val getHotelDetailsUseCase: GetHotelDetailsUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : CoroutineDispatchers {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private val savedStateHandle = SavedStateHandle(mapOf("hotel_id" to 1L))

    private lateinit var viewModel: HotelDetailsViewModel

    private val mockHotel = Hotel(
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
        isFavorite = false
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
    fun `when init then loads hotel details successfully`() = runTest {
        coEvery { getHotelDetailsUseCase(1L) } returns flowOf(DataResult.Success(mockHotel))

        viewModel = HotelDetailsViewModel(
            getHotelDetailsUseCase = getHotelDetailsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            dispatchers = testDispatchers,
            savedStateHandle = savedStateHandle
        )

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            val successState = expectMostRecentItem()
            assertThat(successState.hotel).isEqualTo(mockHotel)
            assertThat(successState.isLoading).isFalse()
            assertThat(successState.error).isNull()
        }
    }

    @Test
    fun `when loadHotelDetails fails then updates error in uiState`() = runTest {
        coEvery { getHotelDetailsUseCase(1L) } returns flowOf(DataResult.Error(DataError.NoInternet))

        viewModel = HotelDetailsViewModel(
            getHotelDetailsUseCase = getHotelDetailsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            dispatchers = testDispatchers,
            savedStateHandle = savedStateHandle
        )

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            val errorState = expectMostRecentItem()
            assertThat(errorState.error).isEqualTo("No internet connection")
            assertThat(errorState.hotel).isNull()
        }
    }

    @Test
    fun `when onToggleFavorite then updates hotel favorite state optimistically and calls usecase`() = runTest {
        coEvery { getHotelDetailsUseCase(1L) } returns flowOf(DataResult.Success(mockHotel))

        viewModel = HotelDetailsViewModel(
            getHotelDetailsUseCase = getHotelDetailsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            dispatchers = testDispatchers,
            savedStateHandle = savedStateHandle
        )

        testScheduler.advanceUntilIdle()

        viewModel.onToggleFavorite(1L)
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.hotel?.isFavorite).isTrue()
        coVerify(exactly = 1) { toggleFavoriteUseCase(1L) }
    }
}
