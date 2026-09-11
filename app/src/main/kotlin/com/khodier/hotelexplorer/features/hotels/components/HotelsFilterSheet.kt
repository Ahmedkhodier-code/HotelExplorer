package com.khodier.hotelexplorer.features.hotels.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.khodier.hotelexplorer.core.designsystem.R
import com.khodier.hotelexplorer.core.designsystem.theme.HotelExplorerTheme
import com.khodier.hotelexplorer.core.domain.model.HotelFilters

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelsFilterSheet(
    initialFilters: HotelFilters,
    valueRange: ClosedFloatingPointRange<Float>,
    onApplyFilters: (HotelFilters) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        HotelsFilterContent(
            initialFilters = initialFilters,
            onApplyFilters = onApplyFilters,
            valueRange = valueRange,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun HotelsFilterContent(
    initialFilters: HotelFilters,
    valueRange: ClosedFloatingPointRange<Float>,
    onApplyFilters: (HotelFilters) -> Unit,
    onDismiss: () -> Unit
) {
    var city by remember { mutableStateOf(initialFilters.city ?: "") }

    val safeValueRange = remember(valueRange) {
        if (valueRange.start > valueRange.endInclusive) {
            0f..1000f
        } else {
            valueRange
        }
    }

    var priceRange by remember {
        val start = (initialFilters.minPrice?.toFloat() ?: safeValueRange.start).coerceIn(safeValueRange)
        val end = (initialFilters.maxPrice?.toFloat() ?: safeValueRange.endInclusive).coerceIn(safeValueRange)
        mutableStateOf(if (start <= end) start..end else start..start)
    }
    var selectedMinRating by remember { mutableStateOf(initialFilters.minRating) }

    val ratingOptions = listOf(null, 3.0, 4.0, 4.5)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.filter_hotels),
            style = MaterialTheme.typography.titleLarge
        )


        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.price_range),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.currency_price_format, priceRange.start.toInt(), stringResource(R.string.currency_egp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.currency_price_format, priceRange.endInclusive.toInt(), stringResource(R.string.currency_egp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RangeSlider(
                value = priceRange,
                onValueChange = { priceRange = it },
                valueRange = safeValueRange,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.min_rating),
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ratingOptions.forEach { rating ->
                    val isSelected = selectedMinRating == rating
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedMinRating = rating },
                        label = {
                            Text(
                                text = if (rating == null) stringResource(R.string.any) else "$rating+",
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = {
                    city = ""
                    priceRange = safeValueRange
                    selectedMinRating = null
                    onApplyFilters(HotelFilters())
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.reset))
            }

            Button(
                onClick = {
                    val newFilters = HotelFilters(
                        city = city.ifBlank { null },
                        minPrice = priceRange.start.toDouble(),
                        maxPrice = priceRange.endInclusive.toDouble(),
                        minRating = selectedMinRating
                    )
                    onApplyFilters(newFilters)
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.apply))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
fun HotelsFilterSheetPreview() {
    HotelExplorerTheme {
        Surface {
            HotelsFilterContent(
                initialFilters = HotelFilters(city = "Paris", minPrice = 50.0),
                onApplyFilters = {},
                valueRange = 0f..1000f,
                onDismiss = {}
            )
        }
    }
}
