package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow


interface SearchTracksInteractor {

    fun searchTracks(expression: String): Flow<SearchTracksResult>

}
