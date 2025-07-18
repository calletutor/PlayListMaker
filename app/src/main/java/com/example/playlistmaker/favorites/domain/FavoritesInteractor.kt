package com.example.playlistmaker.A_NEW.domain

import com.example.playlistmaker.A_NEW.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: TrackEntity)
    suspend fun removeFromFavorites(track: TrackEntity)
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>
    fun getAllFavoriteTrackIds(): Flow<List<Int>>
}
