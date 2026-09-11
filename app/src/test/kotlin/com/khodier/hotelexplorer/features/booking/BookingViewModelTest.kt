package com.khodier.hotelexplorer.features.booking

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.Booking
import com.khodier.hotelexplorer.core.domain.model.BookingConfirmation
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.domain.model.PriceBreakdown
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.booking.CalculateBookingPriceUseCase
import com.khodier.hotelexplorer.core.domain.usecase.booking.ConfirmBookingResult
import com.khodier.hotelexplorer.core.domain.usecase.booking.ConfirmBookingUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelDetailsUseCase
import io.mockk.coEvery
import io.mockk.every
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
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class BookingViewModelTest {

    private val getHotelDetailsUseCase: GetHotelDetailsUseCase = mockk()
    private val calculateBookingPriceUseCase: CalculateBookingPriceUseCase = mockk()
    private val confirmBookingUseCase: ConfirmBookingUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()
    private val testDispatchers = object : CoroutineDispatchers {
        override val io = testDispatcher
        override val main = testDispatcher
        override val default = testDispatcher
    }

    private val savedStateHandle = SavedStateHandle(mapOf("hotel_id" to 1L))

    private lateinit var viewModel: BookingViewModel

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

        viewModel = BookingViewModel(
            getHotelDetailsUseCase,
            calculateBookingPriceUseCase,
            confirmBookingUseCase,
            testDispatchers,
            savedStateHandle
        )

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()
            val successState = expectMostRecentItem()
            assertThat(successState.hotel).isEqualTo(mockHotel)
            assertThat(successState.isLoading).isFalse()
        }
    }

    @Test
    fun `when dates selected then recalculates price`() = runTest {
        coEvery { getHotelDetailsUseCase(1L) } returns flowOf(DataResult.Success(mockHotel))
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val mockBreakdown = PriceBreakdown(2, 1, 500.0, 75.0, 575.0)

        every { calculateBookingPriceUseCase(checkIn, checkOut, 1, 250.0) } returns mockBreakdown

        viewModel = BookingViewModel(
            getHotelDetailsUseCase,
            calculateBookingPriceUseCase,
            confirmBookingUseCase,
            testDispatchers,
            savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.onCheckInSelected(checkIn)
        viewModel.onCheckOutSelected(checkOut)

        assertThat(viewModel.uiState.value.priceBreakdown).isEqualTo(mockBreakdown)
    }

    @Test
    fun `when confirm clicked then success confirmation received`() = runTest {
        coEvery { getHotelDetailsUseCase(1L) } returns flowOf(DataResult.Success(mockHotel))
        val checkIn = LocalDate.now().plusDays(1)
        val checkOut = LocalDate.now().plusDays(3)
        val mockBreakdown = PriceBreakdown(2, 1, 500.0, 75.0, 575.0)
        val mockConfirmation = BookingConfirmation("REF123", Booking(1L, checkIn, checkOut, 1), mockBreakdown)

        every { calculateBookingPriceUseCase(checkIn, checkOut, 1, 250.0) } returns mockBreakdown
        coEvery { confirmBookingUseCase(any(), 250.0) } returns ConfirmBookingResult.Success(mockConfirmation)

        viewModel = BookingViewModel(
            getHotelDetailsUseCase,
            calculateBookingPriceUseCase,
            confirmBookingUseCase,
            testDispatchers,
            savedStateHandle
        )
        testScheduler.advanceUntilIdle()

        viewModel.onCheckInSelected(checkIn)
        viewModel.onCheckOutSelected(checkOut)
        viewModel.onConfirmClick()
        testScheduler.advanceUntilIdle()

        assertThat(viewModel.uiState.value.confirmation).isEqualTo(mockConfirmation)
        assertThat(viewModel.uiState.value.isConfirming).isFalse()
    }
}
