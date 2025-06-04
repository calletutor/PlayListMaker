package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ViewModelSelectedTracks : ViewModel() {

    private val _someData = MutableLiveData<String>("Избранные треки")
    val someData: LiveData<String> = _someData

    fun someFunction(newTitle: String) {
        _someData.value = newTitle
    }

}
