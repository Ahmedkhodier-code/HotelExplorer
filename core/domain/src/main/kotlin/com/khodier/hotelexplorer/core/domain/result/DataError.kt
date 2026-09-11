package com.khodier.hotelexplorer.core.domain.result

sealed class DataError {
    data object NoInternet : DataError()
    data object Timeout : DataError()
    data class ServerError(val code: Int) : DataError()
    data object CacheEmpty : DataError()
    data class Unknown(val message: String? = null) : DataError()
}
