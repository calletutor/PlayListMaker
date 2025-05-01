package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.TracksResult


interface TracksRepository {
    fun searchTracks(expression: String): TracksResult
}
