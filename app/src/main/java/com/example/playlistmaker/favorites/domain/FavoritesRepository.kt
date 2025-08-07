package com.example.playlistmaker.favorites.domain

import com.example.playlistmaker.favorites.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    suspend fun addToFavorites(track: TrackEntity)
    suspend fun removeFromFavorites(track: TrackEntity)
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>
    fun getAllFavoriteTrackIds(): Flow<List<Int>>
    suspend fun isTrackFavorite(trackId: Int): Boolean
    suspend fun getAllFavoriteTrackIdsOnce(): List<Int>
}

