package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.SearchTracksRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch


class SearchTracksInteractorImpl(
    private val repository: SearchTracksRepository
) : SearchTracksInteractor {

    override fun searchTracks(expression: String): Flow<SearchTracksResult> =
        repository.searchTracks(expression)
            .catch {
                emit(SearchTracksResult(emptyList(), isSuccess = false, isNetworkError = true))
            }


}

