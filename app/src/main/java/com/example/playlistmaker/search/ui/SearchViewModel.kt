package com.example.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.data.SearchScreenState
import com.example.playlistmaker.search.domain.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {

    private val _uiState = MutableLiveData(SearchScreenState())
    val uiState: LiveData<SearchScreenState> = _uiState

    var currentQuery: String? = null
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

        _uiState.postValue(_uiState.value?.copy(isLoading = true, error = null))

        viewModelScope.launch {
            tracksInteractor.searchTracks(query)
                .catch { e ->
                    _uiState.postValue(
                        _uiState.value?.copy(
                            tracks = emptyList(),
                            isLoading = false,
                            error = SearchError.Unknown,
                            isShowingHistory = false
                        )
                    )
                }
                .collect { result ->
                    when {
                        result.isSuccess && result.tracks.isNotEmpty() -> {
                            _uiState.postValue(
                                _uiState.value?.copy(
                                    tracks = result.tracks,
                                    isLoading = false,
                                    error = null,
                                    isShowingHistory = false
                                )
                            )
                        }

                        result.isSuccess -> {
                            _uiState.postValue(
                                _uiState.value?.copy(
                                    tracks = emptyList(),
                                    isLoading = false,
                                    error = SearchError.NothingFound,
                                    isShowingHistory = false,
                                    lastQuery = query
                                )
                            )
                        }

                        result.isNetworkError -> {
                            _uiState.postValue(
                                _uiState.value?.copy(
                                    tracks = emptyList(),
                                    isLoading = false,
                                    error = SearchError.Network,
                                    isShowingHistory = false,
                                    lastQuery = query

                                )
                            )
                        }

                        else -> {
                            _uiState.postValue(
                                _uiState.value?.copy(
                                    tracks = emptyList(),
                                    isLoading = false,
                                    error = SearchError.Unknown,
                                    isShowingHistory = false

                                )
                            )
                        }
                    }
                }
        }

    }

    fun loadSearchHistory() {
        searchHistoryInteractor.getSavedHistory(object :
            SearchHistoryInteractor.SearchHistoryConsumer {
            override fun consume(trackList: List<Track>) {
                val hasHistory = trackList.isNotEmpty()
                _uiState.postValue(
                    _uiState.value?.copy(
                        tracks = trackList,
                        hasHistory = trackList.isNotEmpty(),
                        isLoading = false,
                        error = null,
                        isShowingHistory = hasHistory
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
                error = null,
                isShowingHistory = false
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
                error = null,
                isLoading = false
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        searchHandler.removeCallbacksAndMessages(null)
    }

    fun hideHistory() {
        _uiState.postValue(
            _uiState.value?.copy(
                isShowingHistory = false,
                tracks = emptyList()
            )
        )
    }

    fun clearErrorMessage() {
        _uiState.postValue(
            _uiState.value?.copy(error = null)
        )
    }
}
