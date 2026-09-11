package com.khodier.hotelexplorer.features.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.khodier.hotelexplorer.core.designsystem.R
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme
import com.khodier.hotelexplorer.core.domain.model.Booking
import com.khodier.hotelexplorer.core.domain.model.BookingConfirmation
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.core.domain.model.PriceBreakdown
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun BookingSuccessScreen(
    viewModel: BookingViewModel,
    onBackHotelsClick: () -> Unit = {}
) {
    BookingSuccessContent(
        uiState = viewModel.uiState.collectAsState().value,
        onBackHotelsClick = onBackHotelsClick
    )
}

@Composable
fun BookingSuccessContent(
    uiState: BookingUiState,
    onBackHotelsClick: () -> Unit = {},
) {
    val hotel = uiState.hotel
    val confirmation = uiState.confirmation

    Scaffold { padding ->
        if (hotel != null && confirmation != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                SuccessHeader(hotelName = hotel.name)

                Spacer(modifier = Modifier.height(24.dp))

                BookingReferenceCard(reference = confirmation.bookingReference)

                Spacer(modifier = Modifier.height(24.dp))

                BookingDetailsSummaryCard(
                    hotel = hotel,
                    uiState = uiState,
                    totalPrice = confirmation.priceBreakdown.totalPrice
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onBackHotelsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(stringResource(R.string.back_to_hotels), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text(stringResource(R.string.loading_booking_details))
                }
            }
        }
    }
}

@Composable
fun BookingDetailsSummaryCard(hotel: Hotel, uiState: BookingUiState, totalPrice: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column {
            Box(modifier = Modifier.height(180.dp)) {
                AsyncImage(
                    model = hotel.images.firstOrNull(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.7f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = hotel.rating.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = hotel.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = hotel.city, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.5f)),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DateInfo(
                        label = stringResource(R.string.check_in),
                        date = uiState.checkInDate ?: LocalDate.now(),
                        icon = Icons.AutoMirrored.Filled.Login
                    )
                    DateInfo(
                        label = stringResource(R.string.check_out),
                        date = uiState.checkOutDate ?: LocalDate.now().plusDays(1),
                        icon = Icons.AutoMirrored.Filled.Logout
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.total_paid), fontSize = 10.sp, fontWeight = FontWeight.Bold)

                    Text(
                        text = stringResource(R.string.price_format, totalPrice.toInt(), stringResource(R.string.currency_egp)),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun SuccessHeader(hotelName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(58.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.booking_confirmed),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.booking_confirmed_message, hotelName),
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun BookingReferenceCard(reference: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.booking_reference),
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold
                )
                Text(text = reference, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun DateInfo(label: String, date: LocalDate, icon: ImageVector) {
    Column(
        modifier = Modifier
            .border(1.dp,
                MaterialTheme.colorScheme.onSurfaceVariant,
                RoundedCornerShape(16.dp)
            ).padding(16.dp),
        ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = Color(0xFF004D40)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (label == stringResource(R.string.check_in)) stringResource(R.string.check_in_time) else stringResource(R.string.check_out_time),
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true, heightDp = 1200)
@Composable
fun BookingSuccessScreenPreview() {
    val mockHotel = Hotel(
        id = 1,
        name = "The Grand Luminary Hotel",
        city = "London",
        pricePerNight = 280.0,
        rating = 4.9,
        images = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945"),
        description = "Luxury hotel in London.",
        address = "Kensington & Chelsea, London",
        amenities = listOf("WiFi", "Pool"),
        location = HotelLocation(51.5074, 0.1278),
        isFavorite = false
    )

    val mockConfirmation = BookingConfirmation(
        bookingReference = "#ROAM-8492-XJ",
        booking = Booking(
            hotelId = 1,
            checkInDate = LocalDate.of(2025, 10, 14),
            checkOutDate = LocalDate.of(2025, 10, 18),
            roomsCount = 1
        ),
        priceBreakdown = PriceBreakdown(
            nights = 4,
            roomsCount = 1,
            basePrice = 1120.0,
            vatAmount = 168.0,
            totalPrice = 1288.0
        )
    )

    HotelExplorerTheme {
        BookingSuccessContent(
            uiState = BookingUiState(
                hotel = mockHotel,
                isLoading = false,
                checkInDate = LocalDate.of(2025, 10, 14),
                checkOutDate = LocalDate.of(2025, 10, 18),
                priceBreakdown = PriceBreakdown(
                    nights = 4,
                    roomsCount = 1,
                    basePrice = 1120.0,
                    vatAmount = 168.0,
                    totalPrice = 1288.0
                ),
                confirmation = mockConfirmation
            )
        )
    }
}
