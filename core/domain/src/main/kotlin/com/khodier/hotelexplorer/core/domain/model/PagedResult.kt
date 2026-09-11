package com.khodier.hotelexplorer.core.domain.model

data class PagedResult<T>(
    val items: List<T>,
    val currentPage: Int,
    val hasMore: Boolean
)
