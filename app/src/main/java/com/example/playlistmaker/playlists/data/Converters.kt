package com.example.playlistmaker.playlists.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromTrackIdsList(trackIds: List<Int>): String {
        return gson.toJson(trackIds)
    }

    @TypeConverter
    fun toTrackIdsList(data: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(data, listType)
    }
}
