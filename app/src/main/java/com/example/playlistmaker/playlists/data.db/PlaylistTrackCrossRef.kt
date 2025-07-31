package com.example.playlistmaker.playlists.data.db

import androidx.room.Entity

@Entity(primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Int
)
