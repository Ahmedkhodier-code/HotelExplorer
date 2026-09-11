package com.khodier.hotelexplorer.core.domain.result

sealed class DataResult<out T> {
    data class Success<out T>(val data: T, val isFromCache: Boolean = false) : DataResult<T>()
    data class Error(val error: DataError) : DataResult<Nothing>()
}

inline fun <T> DataResult<T>.onSuccess(action: (T) -> Unit): DataResult<T> {
    if (this is DataResult.Success) action(data)
    return this
}

inline fun <T> DataResult<T>.onError(action: (DataError) -> Unit): DataResult<T> {
    if (this is DataResult.Error) action(error)
    return this
}
