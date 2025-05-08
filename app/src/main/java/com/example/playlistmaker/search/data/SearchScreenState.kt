package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.Track

data class SearchScreenState(
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val hasHistory: Boolean = false
)
