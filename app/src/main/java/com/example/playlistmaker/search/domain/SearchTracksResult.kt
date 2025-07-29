package com.example.playlistmaker.search.domain

data class SearchTracksResult(
    val tracks: List<Track>,
    val isSuccess: Boolean,
    val isNetworkError: Boolean
)
