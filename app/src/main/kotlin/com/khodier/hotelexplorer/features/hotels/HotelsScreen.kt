package com.khodier.hotelexplorer.features.hotels

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DensitySmall
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.khodier.hotelexplorer.core.designsystem.R
import com.khodier.hotelexplorer.core.designsystem.components.CachedDataBanner
import com.khodier.hotelexplorer.core.designsystem.components.EmptyState
import com.khodier.hotelexplorer.core.designsystem.components.ErrorState
import com.khodier.hotelexplorer.core.designsystem.components.HotelCard
import com.khodier.hotelexplorer.core.designsystem.components.LoadingState
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme
import com.khodier.hotelexplorer.core.domain.model.Hotel
import com.khodier.hotelexplorer.core.domain.model.HotelFilters
import com.khodier.hotelexplorer.core.domain.model.HotelLocation
import com.khodier.hotelexplorer.features.hotels.components.HotelsFilterSheet
import com.khodier.hotelexplorer.features.hotels.components.HotelsSearchBar

@Composable
fun HotelsScreen(
    viewModel: HotelsViewModel = hiltViewModel(),
    onHotelClick: (Long) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    HotelsScreenContent(
        uiState = uiState,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterChanged = viewModel::onFilterChanged,
        onLoadNextPage = viewModel::onLoadNextPage,
        onToggleFavorite = viewModel::onToggleFavorite,
        onHotelClick = onHotelClick
    )
}

@Composable
fun HotelsScreenContent(
    uiState: HotelsUiState,
    onSearchQueryChanged: (String) -> Unit,
    onFilterChanged: (HotelFilters) -> Unit,
    onLoadNextPage: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onHotelClick: (Long) -> Unit
) {
    val listState = rememberLazyListState()
    var showFilterSheet by remember { mutableStateOf(false) }

    val shouldLoadNextPage = remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = (layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) + 1
            lastVisibleItemIndex > (totalItemsCount - 2) && totalItemsCount > 0
        }
    }

    LaunchedEffect(shouldLoadNextPage.value) {
        if (shouldLoadNextPage.value) {
            onLoadNextPage()
        }
    }

    if (showFilterSheet) {
        val minPrice = uiState.minAvailablePrice.toFloat()
        val maxPrice = uiState.maxAvailablePrice.toFloat()
        val safeRange = if (minPrice <= maxPrice) minPrice..maxPrice else 0f..1000f

        HotelsFilterSheet(
            initialFilters = uiState.filters,
            valueRange = safeRange,
            onApplyFilters = { newFilters ->
                onFilterChanged(newFilters)
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    Scaffold(
        topBar = { HotelsTopBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isFromCache) {
                CachedDataBanner()
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    HotelsSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = onSearchQueryChanged
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilterButton(onShowFilterSheet = { showFilterSheet = true })
            }

            Spacer(modifier = Modifier.height(16.dp))

            CityFilterChips(
                selectedCity = uiState.filters.city,
                cities = uiState.availableCities,
                onCitySelected = { city ->
                    onFilterChanged(uiState.filters.copy(city = city))
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading && uiState.hotels.isEmpty() -> {
                    LoadingState(modifier = Modifier.weight(1f))
                }

                uiState.error != null && uiState.hotels.isEmpty() -> {
                    ErrorState(
                        message = uiState.error,
                        onRetry = { onFilterChanged(uiState.filters) },
                        modifier = Modifier.weight(1f)
                    )
                }

                uiState.hotels.isEmpty() -> {
                    EmptyState(modifier = Modifier.weight(1f))
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.hotels,
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

                        if (uiState.isLoadingNextPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HotelsTopBar() {
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
                    text = stringResource(R.string.hotels),
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

@Composable
fun FilterButton(onShowFilterSheet: () -> Unit) {
    Surface(
        modifier = Modifier.size(48.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier.clickable { onShowFilterSheet() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.FilterAlt,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun CityFilterChips(
    selectedCity: String?,
    cities: List<String>,
    onCitySelected: (String?) -> Unit
) {
    val allCitiesLabel = stringResource(R.string.all_cities)
    val chips = listOf(allCitiesLabel) + cities

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { city ->
            val isSelected = if (city == allCitiesLabel) selectedCity == null else selectedCity == city
            val backgroundColor =
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
            val contentColor =
                if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Surface(
                modifier = Modifier.clickable {
                    onCitySelected(if (city == allCitiesLabel) null else city)
                },
                shape = RoundedCornerShape(12.dp),
                color = backgroundColor,
                shadowElevation = if (isSelected) 0.dp else 1.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (city == allCitiesLabel) {
                        Icon(
                            imageVector = Icons.Filled.DensitySmall,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = contentColor
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        text = city,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HotelsScreenPreview() {
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
        HotelsScreenContent(
            uiState = HotelsUiState(
                hotels = mockHotels,
                availableCities = listOf("Cairo", "Giza", "Alexandria", "Luxor", "Aswan"),
                isLoading = false
            ),
            onSearchQueryChanged = {},
            onFilterChanged = {},
            onLoadNextPage = {},
            onToggleFavorite = {},
            onHotelClick = {}
        )
    }
}
