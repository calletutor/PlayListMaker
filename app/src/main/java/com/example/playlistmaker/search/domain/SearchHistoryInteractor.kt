package com.example.playlistmaker.search.domain


interface SearchHistoryInteractor {
    suspend fun getSavedHistory(consumer: SearchHistoryConsumer)
    fun addTrackToHistory(track: Track)
    fun saveTrackListToHistory(trackList: List<Track>)
    fun clearTrackListOfHistory()

    interface SearchHistoryConsumer {
        fun consume(trackList: List<Track>)
    }
}
