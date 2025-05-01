package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.Track


interface SearchHistoryInteractor {
    fun getSavedHistory(consumer: SearchHistoryConsumer)
    fun addTrackToHistory(track: Track)
    fun saveTrackListToHistory(trackList: List<Track>)
    fun clearTrackListOfHistory()

    interface SearchHistoryConsumer {
        fun consume(trackList: List<Track>)
    }
}
