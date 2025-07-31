package com.example.playlistmaker.playlists.data

import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.playlists.data.db.PlaylistTrackEntity

fun Track.toEntity(): TrackEntity {
    return TrackEntity(
        trackId = this.trackId,
        isFavorite = this.isFavorite,
        previewUrl = this.previewUrl,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        country = this.country,
        primaryGenreName = this.primaryGenreName,
        addedAt = System.currentTimeMillis()
    )
}

fun TrackEntity.toPlaylistTrackEntity(): PlaylistTrackEntity {
    return PlaylistTrackEntity(
        trackId = this.trackId,
        isFavorite = this.isFavorite,
        previewUrl = this.previewUrl,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        country = this.country,
        primaryGenreName = this.primaryGenreName,
        addedAt = System.currentTimeMillis()
    )
}

