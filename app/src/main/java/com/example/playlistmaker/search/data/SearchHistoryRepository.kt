package com.example.playlistmaker.search.data

import com.example.playlistmaker.player.domain.Track


interface SearchHistoryRepository {
    fun getSavedHistoryList(): List<Track>
    fun saveTrackListToHistory(trackList: List<Track>)
}
