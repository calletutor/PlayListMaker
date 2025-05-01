package com.example.playlistmaker.player.domain

import com.example.playlistmaker.search.data.TracksSearchResponse

class TrackMapper {
    fun map(response: TracksSearchResponse): List<Track> {
        return response.results.map {
            Track(
                previewUrl = it.previewUrl,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTimeMillis = it.trackTimeMillis,
                artworkUrl100 = it.artworkUrl100,
                trackId = it.trackId,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                country = it.country,
                primaryGenreName = it.primaryGenreName
            )
        }
    }
}
