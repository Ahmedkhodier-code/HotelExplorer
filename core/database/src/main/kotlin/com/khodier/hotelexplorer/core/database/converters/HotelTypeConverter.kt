package com.khodier.hotelexplorer.core.database.converters

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class HotelTypeConverter {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val type = Types.newParameterizedType(List::class.java, String::class.java)
    private val adapter = moshi.adapter<List<String>>(type)

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        return adapter.toJson(value ?: emptyList())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        return if (value.isNullOrEmpty()) emptyList() else adapter.fromJson(value) ?: emptyList()
    }
}