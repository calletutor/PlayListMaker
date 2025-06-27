package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.data.SearchScreenState
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.domain.TracksResult




class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData(SearchScreenState())
    val uiState: LiveData<SearchScreenState> = _uiState

    private var currentQuery: String? = null
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    fun searchDebounce(query: String) {

        if (query.isBlank()) return

        currentQuery = query
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        searchRunnable = Runnable { search(query) }
        searchHandler.postDelayed(searchRunnable!!, 2000L)
    }

    private fun search(query: String) {
        _uiState.postValue(_uiState.value?.copy(isLoading = true, errorMessage = null))

        tracksInteractor.searchTracks(query, object : TracksInteractor.TracksConsumer {
            override fun consume(result: TracksResult) {
                when {
                    result.isSuccess && result.tracks.isNotEmpty() -> {
                        _uiState.postValue(
                            _uiState.value?.copy(
                                tracks = result.tracks,
                                isLoading = false,
                                errorMessage = null
                            )
                        )
                    }

                    result.isSuccess -> {
                        _uiState.postValue(
                            _uiState.value?.copy(
                                tracks = emptyList(),
                                isLoading = false,
                                errorMessage = "Ничего не найдено."
                            )
                        )
                    }

                    result.isNetworkError -> {
                        _uiState.postValue(
                            _uiState.value?.copy(
                                tracks = emptyList(),
                                isLoading = false,
                                errorMessage = "Проблема с сетью."
                            )
                        )
                    }

                    else -> {
                        _uiState.postValue(
                            _uiState.value?.copy(
                                tracks = emptyList(),
                                isLoading = false,
                                errorMessage = "Неизвестная ошибка."
                            )
                        )
                    }
                }
            }
        })
    }

    fun loadSearchHistory() {
        searchHistoryInteractor.getSavedHistory(object :
            SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(trackList: List<Track>) {
                _uiState.postValue(
                    _uiState.value?.copy(
                        tracks = trackList,
                        hasHistory = trackList.isNotEmpty(),
                        isLoading = false,
                        errorMessage = null
                    )
                )
            }
        })
    }

    fun saveTrackToHistory(track: Track) {
        searchHistoryInteractor.addTrackToHistory(track)
    }

    fun clearHistory() {
        searchHistoryInteractor.clearTrackListOfHistory()
        _uiState.postValue(
            _uiState.value?.copy(
                tracks = emptyList(),
                hasHistory = false,
                errorMessage = null
            )
        )
    }

    fun retrySearch() {
        val query = currentQuery
        if (!query.isNullOrEmpty()) {
            searchDebounce(query)
        }
    }

    fun cancelSearchDebounce() {
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        currentQuery = null
    }

    fun clearTracks() {
        _uiState.postValue(
            _uiState.value?.copy(
                tracks = emptyList(),
                errorMessage = null
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        searchHandler.removeCallbacksAndMessages(null)
    }
}
