package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.search.ui.SearchError

data class SearchScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val error: SearchError? = null,
    val hasHistory: Boolean = false,
    val isShowingHistory: Boolean = false,
    val lastQuery: String? = null
)



