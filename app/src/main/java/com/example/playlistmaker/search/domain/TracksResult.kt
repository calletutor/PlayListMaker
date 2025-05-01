package com.example.playlistmaker.search.domain

import com.example.playlistmaker.player.domain.Track

data class TracksResult(
    val tracks: List<Track>,
    val isSuccess: Boolean,
    val isNetworkError: Boolean
)
