package com.example.playlistmaker.favorites.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(

    @PrimaryKey(autoGenerate = true)
    val playlistId: Long = 0,

    val name: String,
    val description: String?,
    val coverImagePath: String?,

    val tracksCount: Int = 0
)

