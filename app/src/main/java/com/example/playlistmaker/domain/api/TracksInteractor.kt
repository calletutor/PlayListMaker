package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.TracksResult


interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(result: TracksResult)
    }
}
