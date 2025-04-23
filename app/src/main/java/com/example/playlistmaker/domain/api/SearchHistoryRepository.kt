package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track


interface SearchHistoryRepository {
    fun getSavedHistoryList(): List<Track>
    fun saveTrackListToHistory(trackList: List<Track>)
}
