package com.example.playlistmaker.search.domain

import kotlinx.coroutines.flow.Flow


interface TracksInteractor {

    fun searchTracks(expression: String): Flow<TracksResult>

}
