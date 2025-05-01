package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor // Добавляем новый интерактор

) : ViewModel() {

    private val _darkThemeEnabled = MutableLiveData<Boolean>()
    val darkThemeEnabled: LiveData<Boolean> = _darkThemeEnabled

    private val _event = MutableLiveData<SettingsEvent>()
    val event: LiveData<SettingsEvent> = _event

    init {
        loadDarkThemeState()
    }

    private fun loadDarkThemeState() {
        settingsInteractor.darkThemeIsEnabled(object : SettingsInteractor.DarkThemeConsumer {
            override fun consume(darkThemeIsEnabled: Boolean) {
                _darkThemeEnabled.postValue(darkThemeIsEnabled)
            }
        })
    }

    fun onDarkThemeSwitched(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        settingsInteractor.applyDarkTheme(enabled)
    }

    fun onShareAppClicked() {
        sharingInteractor.shareApp()
    }

    fun onSupportClicked() {
        sharingInteractor.openSupport()
    }

    fun onAgreementClicked() {
        sharingInteractor.openTerms()
    }
}
