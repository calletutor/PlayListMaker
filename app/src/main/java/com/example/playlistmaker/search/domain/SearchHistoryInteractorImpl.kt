package com.example.playlistmaker.search.domain

import com.example.playlistmaker.favorites.domain.FavoritesRepository
import kotlinx.coroutines.flow.firstOrNull


class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository,
                                  private val favoritesRepository: FavoritesRepository
) :
    SearchHistoryInteractor {

    companion object {
        const val HISTORY_SIZE = 10
    }

    override suspend fun getSavedHistory(consumer: SearchHistoryInteractor.SearchHistoryConsumer) {
        val history = repository.getSavedHistoryList()
        val favoriteTracks = favoritesRepository.getAllFavoriteTracks().firstOrNull() ?: emptyList()

        val updatedHistory = history.map { track ->
            track.isFavorite = favoriteTracks.any { it.trackId == track.trackId }
            track
        }

        consumer.consume(updatedHistory)
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
