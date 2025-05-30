package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import com.example.playlistmaker.main.ui.DARK_THEME
import com.example.playlistmaker.settings.domain.SettingsRepository

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun isThereThemeHistorySaved(): Boolean {
        return sharedPreferences.contains(DARK_THEME)
    }

    override fun isDarkThemeEnabled(): Boolean {
        return sharedPreferences.getBoolean(DARK_THEME, false)
    }

    override fun setDarkThemeEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean(DARK_THEME, enabled).apply()
    }
}
