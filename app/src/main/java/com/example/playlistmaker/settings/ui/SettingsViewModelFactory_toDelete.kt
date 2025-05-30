package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModelFactory_toDelete(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor // Добавляем параметр
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(settingsInteractor, sharingInteractor) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
