package com.khodier.hotelexplorer.core.domain.model

data class HotelFilters(
    val searchQuery: String? = null,
    val city: String? = null,
    val minRating: Double? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null
) {
    val isEmpty: Boolean
        get() = searchQuery == null && city == null && minRating == null && minPrice == null && maxPrice == null
}
