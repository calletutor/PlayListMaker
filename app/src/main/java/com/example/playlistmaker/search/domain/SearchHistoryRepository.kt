package com.example.playlistmaker.search.domain


interface SearchHistoryRepository {
    fun getSavedHistoryList(): List<Track>
    fun saveTrackListToHistory(trackList: List<Track>)
}
