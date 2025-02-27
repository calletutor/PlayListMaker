package com.example.playlistmaker

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat.getSystemService

const val DARK_THEME = "dark_theme"

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        ScreenModeHandler.setCurrentScreenMode(this)

    }
}
