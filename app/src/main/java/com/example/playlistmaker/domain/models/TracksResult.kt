package com.example.playlistmaker.domain.models

data class TracksResult(
    val tracks: List<Track>,
    val isSuccess: Boolean,
    val isNetworkError: Boolean
)
