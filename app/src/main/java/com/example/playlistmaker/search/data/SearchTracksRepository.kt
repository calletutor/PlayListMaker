package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.SearchTracksResult
import kotlinx.coroutines.flow.Flow

interface SearchTracksRepository {
    fun searchTracks(expression: String): Flow<SearchTracksResult>
}
