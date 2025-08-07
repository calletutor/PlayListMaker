package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//не зайдествован, возможно, нужно удалить

class PlayListsViewModel : ViewModel() {

    private val _someData = MutableLiveData<String>("Плейлисты")
    val someData: LiveData<String> = _someData

    fun someFunction(name: String) {
        _someData.value = name
    }
}
