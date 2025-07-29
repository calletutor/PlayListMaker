package com.example.playlistmaker.favorites.domain

import com.example.playlistmaker.favorites.data.db.TrackEntity
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow



class FavoritesInteractorImpl(
    private val repository: FavoritesRepository
) : FavoritesInteractor {

    override suspend fun addToFavorites(track: Track) {
        repository.addToFavorites(track.toTrackEntity())
    }

    override suspend fun removeFromFavorites(track: Track) {
        repository.removeFromFavorites(track.toTrackEntity())
    }

    override fun getAllFavoriteTracks(): Flow<List<TrackEntity>> {
        return repository.getAllFavoriteTracks()
    }

    override fun getAllFavoriteTrackIds(): Flow<List<Int>> {
        return repository.getAllFavoriteTrackIds()
    }


    override suspend fun isTrackFavorite(trackId: Int): Boolean {
        return repository.isTrackFavorite(trackId)
    }

    private fun Track.toTrackEntity(): TrackEntity = TrackEntity(
        isFavorite = this.isFavorite,
        previewUrl = this.previewUrl,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTimeMillis = this.trackTimeMillis,
        artworkUrl100 = this.artworkUrl100,
        trackId = this.trackId,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        country = this.country,
        primaryGenreName = this.primaryGenreName,
        addedAt = System.currentTimeMillis()
    )

}

