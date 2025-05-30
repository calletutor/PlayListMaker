package com.example.playlistmaker.settings.domain

interface SettingsRepository {
    fun isThereThemeHistorySaved(): Boolean
    fun isDarkThemeEnabled(): Boolean
    fun setDarkThemeEnabled(enabled: Boolean)
}
