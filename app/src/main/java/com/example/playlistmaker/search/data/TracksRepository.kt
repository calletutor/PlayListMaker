package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.TracksResult
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<TracksResult>
}
