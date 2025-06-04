package com.example.playlistmaker.settings.ui

import android.content.Context
import android.content.res.Configuration
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

    fun loadInitialThemeState(context: Context) {
        settingsInteractor.wasThemeSaved(object : SettingsInteractor.SavedThemeHistoryConsumer {
            override fun consume(isThemeHistory: Boolean) {
                if (isThemeHistory) {
                    loadDarkThemeState()
                } else {
                    val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                    val isDark = nightModeFlags == Configuration.UI_MODE_NIGHT_YES

                    _uiState.postValue(
                        _uiState.value?.copy(isDarkThemeEnabled = isDark)
                    )
                }
            }
        })
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

