package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.search.domain.TracksResult

class SearchViewModel(

    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor

) : ViewModel() {


    private val _hasHistory = MutableLiveData(false)
    val hasHistory: LiveData<Boolean> = _hasHistory

    private var currentQuery: String? = null

    private val _tracks = MutableLiveData<List<Track>>(emptyList())
    val tracks: LiveData<List<Track>> = _tracks

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun searchDebounce(query: String) {
        currentQuery = query
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable { search(query) }
        searchHandler.postDelayed(searchRunnable!!, 2000L)
    }

    private fun search(query: String) {

        _loading.postValue(true)
        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: TracksResult) {
                _loading.postValue(false)
                when {
                    result.isSuccess && result.tracks.isNotEmpty() -> {
                        _tracks.postValue(result.tracks)
                        _error.postValue(null)
                    }
                    result.isSuccess -> {
                        _tracks.postValue(emptyList())
                        _error.postValue("Ничего не найдено.")
                    }
                    result.isNetworkError -> {
                        _tracks.postValue(emptyList())
                        _error.postValue("Проблема с сетью.")
                    }
                }
            }
        })
    }

    fun loadSearchHistory() {
        searchHistoryInteractor.getSavedHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(trackList: List<Track>) {
                _tracks.postValue(trackList)
            }
        })
    }

    fun clearHistory() {
        searchHistoryInteractor.clearTrackListOfHistory()
        _tracks.postValue(emptyList())
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }

    override fun onCleared() {
        super.onCleared()
        searchHandler.removeCallbacksAndMessages(null)
    }

    fun retrySearch() {
        val query = currentQuery
        if (!query.isNullOrEmpty()) {
            searchDebounce(query)
        }
    }

    fun onClearButtonClicked() {
        _tracks.value = emptyList()
        _error.value = null
    }

    fun cancelSearchDebounce() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        currentQuery = null
    }

    fun loadHasHistory() {
        searchHistoryInteractor.getSavedHistory(object : SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(trackList: List<Track>) {
                _hasHistory.postValue(trackList.isNotEmpty())
            }
        })
    }

    fun clearTracks() {
        _tracks.value = emptyList()
    }


}
