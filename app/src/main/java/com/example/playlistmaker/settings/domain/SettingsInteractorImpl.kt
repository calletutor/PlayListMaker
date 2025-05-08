package com.example.playlistmaker.settings.domain

import androidx.appcompat.app.AppCompatDelegate

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun darkThemeIsEnabled(consumer: SettingsInteractor.DarkThemeConsumer) {
        consumer.consume(settingsRepository.isDarkThemeEnabled())
    }

    override fun setDarkTheme(enabled: Boolean) {
        settingsRepository.setDarkThemeEnabled(enabled)
    }

    override fun applyDarkTheme(darkThemeIsEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeIsEnabled) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}
