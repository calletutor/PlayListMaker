package com.example.playlistmaker.domain.impl

import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

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
