package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksResult


interface TracksRepository {
    fun searchTracks(expression: String): TracksResult
}
