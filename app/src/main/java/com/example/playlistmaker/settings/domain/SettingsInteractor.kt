package com.example.playlistmaker.settings.domain

interface SettingsInteractor {

    fun wasThemeSaved(consumer: SavedThemeHistoryConsumer)
    fun darkThemeIsEnabled(consumer: DarkThemeConsumer)
    fun setDarkTheme(enabled: Boolean)
    fun applyDarkTheme(darkThemeIsEnabled: Boolean)

    interface DarkThemeConsumer {
        fun consume(darkThemeIsEnabled: Boolean)
    }

    interface SavedThemeHistoryConsumer {
        fun consume(isThemeHistory: Boolean)
    }
}
