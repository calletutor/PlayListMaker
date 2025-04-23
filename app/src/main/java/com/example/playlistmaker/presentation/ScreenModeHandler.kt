package com.example.playlistmaker.presentation

import com.example.playlistmaker.domain.api.SettingsInteractor

class ScreenModeHandler(
    private val settingsInteractor: SettingsInteractor
) {

    fun setCurrentScreenMode() {
        settingsInteractor.darkThemeIsEnabled(object : SettingsInteractor.DarkThemeConsumer {
            override fun consume(darkThemeIsEnabled: Boolean) {
                if (darkThemeIsEnabled) {
                    settingsInteractor.applyDarkTheme(true)
                }
            }
        })
    }
}
