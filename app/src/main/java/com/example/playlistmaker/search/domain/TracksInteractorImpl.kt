package com.example.playlistmaker.search.domain

import com.example.playlistmaker.search.data.TracksRepository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch


class TracksInteractorImpl(
    private val repository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<TracksResult> =
        repository.searchTracks(expression)
            .catch {
                emit(TracksResult(emptyList(), isSuccess = false, isNetworkError = true))
            }




}

