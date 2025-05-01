package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.main.ui.TRACK_HISTORY
import com.example.playlistmaker.player.domain.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(
    private val sharedPref: SharedPreferences,
    private val gson: Gson
): SearchHistoryRepository {

    override fun getSavedHistoryList(): List<Track> {
        val str = sharedPref.getString(TRACK_HISTORY, null)
        var trackList = mutableListOf<Track>()
        if (str != null) {
            trackList = mutableListOf<Track>().apply {
                addAll(gson.fromJson(str, Array<Track>::class.java))
            }
        }
        return trackList
    }

    override fun saveTrackListToHistory(trackList: List<Track>) {
        val str = gson.toJson(trackList)
        sharedPref.edit()
            .putString(TRACK_HISTORY, str)
            .apply()
    }
}
