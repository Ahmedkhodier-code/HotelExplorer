package com.khodier.hotelexplorer.features.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.khodier.hotelexplorer.core.designsystem.R
import com.khodier.hotelexplorer.core.designsystem.components.EmptyState
import com.khodier.hotelexplorer.core.designsystem.components.HotelCard
import com.khodier.hotelexplorer.core.designsystem.components.LoadingState
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelLocation

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = hiltViewModel(),
    onHotelClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    FavoritesScreenContent(
        uiState = uiState,
        onToggleFavorite = viewModel::onToggleFavorite,
        onHotelClick = onHotelClick
    )
}

@Composable
fun FavoritesScreenContent(
    uiState: FavoritesUiState,
    onToggleFavorite: (Long) -> Unit,
    onHotelClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()

    Scaffold(
        topBar = { FavoritesHotelsTopBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when {
                uiState.isLoading -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }

                uiState.favorites.isEmpty() -> {
                    EmptyState(
                        title = stringResource(R.string.empty_favorites_title),
                        message = stringResource(R.string.empty_favorites_message),
                        modifier = Modifier.weight(1f)
                    )
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.favorites,
                            key = { it.id }
                        ) { hotel ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                HotelCard(
                                    hotel = hotel,
                                    onClick = { onHotelClick(hotel.id) },
                                    onToggleFavorite = onToggleFavorite
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FavoritesHotelsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "H",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = stringResource(R.string.favorites_hotel),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = stringResource(R.string.explore_hotels),
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    val mockHotels = listOf(
        Hotel(
            id = 1,
            name = "The Grand Luminary",
            city = "Cairo",
            pricePerNight = 3200.0,
            rating = 4.9,
            images = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945"),
            amenities = listOf("Free WiFi", "Swimming Pool", "Restaurant"),
            isFavorite = true,
            description = "Luxury hotel overlooking the Nile.",
            address = "Corniche El Nile, Cairo, Egypt",
            location = HotelLocation(30.0444, 31.2357)
        ),
        Hotel(
            id = 2,
            name = "Pyramids View Resort",
            city = "Giza",
            pricePerNight = 2800.0,
            rating = 4.7,
            images = listOf("https://images.unsplash.com/photo-1548013146-72479768bada"),
            amenities = listOf("Free WiFi", "Pyramid View", "Airport Shuttle"),
            isFavorite = true,
            description = "A scenic resort near the Giza Pyramids.",
            address = "Pyramids Road, Giza, Egypt",
            location = HotelLocation(29.9792, 31.1342)
        )
    )

    HotelExplorerTheme {
        FavoritesScreenContent(
            uiState = FavoritesUiState(
                favorites = mockHotels,
                isLoading = false
            ),
            onToggleFavorite = {},
            onHotelClick = {}
        )
    }
}
