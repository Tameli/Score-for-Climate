package com.example.scoreforclimate.roomDB

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.sql.Date


class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun restoreList(listOfString: String?): MutableList<String?>? {
        return Gson().fromJson(
            listOfString,
            object : TypeToken<MutableList<String?>?>() {}.type
        )
    }

    @TypeConverter
    fun saveList(listOfString: MutableList<String?>?): String? {
        return Gson().toJson(listOfString)
    }
}