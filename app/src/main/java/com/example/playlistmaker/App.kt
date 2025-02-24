package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

const val DARK_THEME = "dark_theme"

class App : Application() {

    var darkTheme: Boolean = false
    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(DARK_THEME, MODE_PRIVATE)

        if (sharedPrefs.getString(DARK_THEME, "") == "true") {
            darkTheme = true
        } else {
            darkTheme = false
        }
        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
