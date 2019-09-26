package com.untha.model.converters

import androidx.room.TypeConverter
import java.util.*


object DateConverter {

    @TypeConverter
    @JvmStatic
    fun toDate(dateLong: Long): Date {
        return Date(dateLong)
    }

    @TypeConverter
    @JvmStatic
    fun fromDate(date: Date): Long {
        return date.time
    }
}
