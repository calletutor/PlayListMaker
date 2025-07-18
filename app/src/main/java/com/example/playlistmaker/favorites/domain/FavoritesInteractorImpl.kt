package com.example.playlistmaker.A_NEW.domain

import com.example.playlistmaker.A_NEW.data.db.TrackEntity
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: TrackEntity) {
        repository.addToFavorites(track)
    }

    override suspend fun removeFromFavorites(track: TrackEntity) {
        repository.removeFromFavorites(track)
    }

    override fun getAllFavoriteTracks(): Flow<List<TrackEntity>> {
        return repository.getAllFavoriteTracks()
    }

    override fun getAllFavoriteTrackIds(): Flow<List<Int>> {
        return repository.getAllFavoriteTrackIds()
    }

}

