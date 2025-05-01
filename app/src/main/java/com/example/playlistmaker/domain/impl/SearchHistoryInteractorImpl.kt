package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.api.SearchHistoryRepository


class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository) :
    SearchHistoryInteractor {

    companion object {
        const val HISTORY_SIZE = 10
    }

    override fun getSavedHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        val history = repository.getSavedHistoryList()
        consumer.consume(history)
    }

    override fun addTrackToHistory(track: Track) {

        val trackList = repository.getSavedHistoryList().toMutableList()

        val index = getIndexOfTrack(track.trackId, trackList)
        if (index != null) {
            trackList.removeAt(index)
        }else if (trackList.size >= HISTORY_SIZE) {
            trackList.removeAt(trackList.lastIndex)
        }
        trackList.add(0, track)
        saveTrackListToHistory(trackList)
    }

    override fun saveTrackListToHistory(trackList: List<Track>) {
        repository.saveTrackListToHistory(trackList)
    }

    override fun clearTrackListOfHistory() {
        saveTrackListToHistory(emptyList<Track>())
    }

    private fun getIndexOfTrack(trackId: Int, trackList: MutableList<Track>): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }
}
