package com.example.playlistmaker.search.domain

data class TracksResult(
    val tracks: List<Track>,
    val isSuccess: Boolean,
    val isNetworkError: Boolean
)
