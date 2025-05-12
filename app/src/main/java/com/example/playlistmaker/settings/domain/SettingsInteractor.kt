package com.example.playlistmaker.settings.domain

interface SettingsInteractor {
    fun darkThemeIsEnabled(consumer: DarkThemeConsumer)
    fun setDarkTheme(enabled: Boolean)
    fun applyDarkTheme(darkThemeIsEnabled: Boolean)

    interface DarkThemeConsumer {
        fun consume(darkThemeIsEnabled: Boolean)
    }
}
