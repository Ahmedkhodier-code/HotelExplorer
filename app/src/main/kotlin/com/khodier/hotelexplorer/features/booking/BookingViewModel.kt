package com.khodier.hotelexplorer.features.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.khodier.hotelexplorer.core.common.dispatcher.CoroutineDispatchers
import com.khodier.hotelexplorer.core.domain.model.Booking
import com.khodier.hotelexplorer.core.domain.result.DataError
import com.khodier.hotelexplorer.core.domain.result.DataResult
import com.khodier.hotelexplorer.core.domain.usecase.booking.CalculateBookingPriceUseCase
import com.khodier.hotelexplorer.core.domain.usecase.booking.ConfirmBookingResult
import com.khodier.hotelexplorer.core.domain.usecase.booking.ConfirmBookingUseCase
import com.khodier.hotelexplorer.core.domain.usecase.hotels.GetHotelDetailsUseCase
import com.khodier.hotelexplorer.core.domain.usecase.booking.BookingDatesValidationResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val calculateBookingPriceUseCase: CalculateBookingPriceUseCase,
    private val confirmBookingUseCase: ConfirmBookingUseCase,
    private val dispatchers: CoroutineDispatchers,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val hotelId: Long?
        get() = savedStateHandle.get<Long>("hotel_id")

    init {
        loadHotel()
    }

    private fun loadHotel() {
        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isLoading = true, error = null) }
            hotelId?.let { id ->
                when (val result = getHotelDetailsUseCase(id).first()) {
                    is DataResult.Success -> {
                        _uiState.update { it.copy(hotel = result.data, isLoading = false) }
                    }

                    is DataResult.Error -> {
                        val message = when (val error = result.error) {
                            is DataError.NoInternet -> "No internet connection"
                            is DataError.Timeout -> "Request timed out"
                            is DataError.ServerError -> "Server error (${error.code})"
                            is DataError.CacheEmpty -> "No cached data available"
                            is DataError.Unknown -> error.message ?: "Unknown error occurred"
                        }
                        _uiState.update { it.copy(isLoading = false, error = message) }
                    }
                }
            }
        }
    }

    fun onCheckInSelected(date: LocalDate) {
        _uiState.update { it.copy(checkInDate = date, dateError = null) }
        recalculatePrice()
    }

    fun onCheckOutSelected(date: LocalDate) {
        _uiState.update { it.copy(checkOutDate = date, dateError = null) }
        recalculatePrice()
    }

    fun onRoomsCountChanged(count: Int) {
        if (count < 1) return
        _uiState.update { it.copy(roomsCount = count) }
        recalculatePrice()
    }

    private fun recalculatePrice() {
        val state = _uiState.value
        val checkIn = state.checkInDate
        val checkOut = state.checkOutDate
        val hotel = state.hotel

        if (checkIn != null && checkOut != null && hotel != null && checkOut.isAfter(checkIn)) {
            val breakdown = calculateBookingPriceUseCase(
                checkIn = checkIn,
                checkOut = checkOut,
                roomsCount = state.roomsCount,
                pricePerNight = hotel.pricePerNight
            )
            _uiState.update { it.copy(priceBreakdown = breakdown) }
        } else {
            _uiState.update { it.copy(priceBreakdown = null) }
        }
    }

    fun onConfirmClick() {
        val state = _uiState.value
        val hotel = state.hotel
        val checkIn = state.checkInDate
        val checkOut = state.checkOutDate

        if (hotel == null || checkIn == null || checkOut == null) return

        viewModelScope.launch(dispatchers.io) {
            _uiState.update { it.copy(isConfirming = true, dateError = null) }

            val booking = Booking(
                hotelId = hotel.id,
                checkInDate = checkIn,
                checkOutDate = checkOut,
                roomsCount = state.roomsCount
            )

            when (val result = confirmBookingUseCase(booking, hotel.pricePerNight)) {
                is ConfirmBookingResult.Success -> {
                    _uiState.update {
                        it.copy(isConfirming = false, confirmation = result.confirmation)
                    }
                }

                is ConfirmBookingResult.InvalidDates -> {
                    val message = when (result.reason) {
                        is BookingDatesValidationResult.CheckInInPast ->
                            "Check-in date cannot be in the past"

                        is BookingDatesValidationResult.CheckOutBeforeOrEqualCheckIn ->
                            "Check-out date must be after check-in"

                        is BookingDatesValidationResult.Valid -> "Invalid booking dates"
                    }
                    _uiState.update { it.copy(isConfirming = false, dateError = message) }
                }
            }
        }
    }
}