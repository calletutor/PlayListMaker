package com.example.playlistmaker.playlists.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_tracks")
data class PlaylistTrackEntity(
    @PrimaryKey
    val trackId: Int,
    var isFavorite: Boolean,
//    var isFavorite: Boolean = false,
    val previewUrl: String?,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: String,
    val artworkUrl100: String,
    val collectionName: String,
    val releaseDate: String,
    val country: String,
    val primaryGenreName: String,
    val addedAt: Long,

)