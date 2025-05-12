package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {
    private val _uiState = MutableLiveData(SettingsScreenState())
    val uiState: LiveData<SettingsScreenState> = _uiState

    init {
        loadDarkThemeState()
    }

    private fun loadDarkThemeState() {
        settingsInteractor.darkThemeIsEnabled(object : SettingsInteractor.DarkThemeConsumer {
            override fun consume(darkThemeIsEnabled: Boolean) {
                _uiState.postValue(
                    _uiState.value?.copy(isDarkThemeEnabled = darkThemeIsEnabled)
                )
            }
        })
    }

    fun onDarkThemeSwitched(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        settingsInteractor.applyDarkTheme(enabled)
        _uiState.postValue(
            _uiState.value?.copy(isDarkThemeEnabled = enabled)
        )
    }

    fun onShareAppClicked() {
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = SettingsEvent.ShareApp)
        )
        sharingInteractor.shareApp()
        // Сбрасываем событие после обработки
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = null)
        )
    }

    fun onSupportClicked() {
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = SettingsEvent.Support)
        )
        sharingInteractor.openSupport()
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = null)
        )
    }

    fun onAgreementClicked() {
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = SettingsEvent.OpenAgreement)
        )
        sharingInteractor.openTerms()
        _uiState.postValue(
            _uiState.value?.copy(currentEvent = null)
        )
    }
}

