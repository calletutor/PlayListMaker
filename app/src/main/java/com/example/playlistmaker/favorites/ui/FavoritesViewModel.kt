package com.example.playlistmaker.A_NEW.ui

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.A_NEW.data.db.TrackEntity
import com.example.playlistmaker.A_NEW.domain.FavoritesInteractor
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.flow.Flow

class FavoritesViewModel(
    private val interactor: FavoritesInteractor
) : ViewModel() {

    suspend fun addToFavorites(track: Track) {
        interactor.addToFavorites(track.toTrackEntity())
    }

    suspend fun removeFromFavorites(track: Track) {
        interactor.removeFromFavorites(track.toTrackEntity())
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

