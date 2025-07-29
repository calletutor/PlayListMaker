package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.favorites.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.toTrack
import kotlinx.coroutines.flow.map


class ViewModelSelectedTracks(private val favoritesRepository: FavoritesRepository) : ViewModel() {

    val favoriteTracks: LiveData<List<Track>> = favoritesRepository.getAllFavoriteTracks()
        .map { list -> list.map { it.toTrack() } }
        .asLiveData()

}
