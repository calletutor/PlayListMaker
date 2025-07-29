package com.example.playlistmaker.playlists.data.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.playlistmaker.favorites.data.db.PlaylistEntity

// Плейлист с треками
data class PlaylistWithTracks(
    @Embedded val playlist: PlaylistEntity,
    @Relation(
        parentColumn = "playlistId",
        entityColumn = "trackId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val tracks: List<PlaylistTrackEntity>
)

// Трек с плейлистами
data class TrackWithPlaylists(
    @Embedded val track: PlaylistTrackEntity,
    @Relation(
        parentColumn = "trackId",
        entityColumn = "playlistId",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    val playlists: List<PlaylistEntity>
)
