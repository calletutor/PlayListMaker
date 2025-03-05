package com.example.playlistmaker

import android.app.Application

const val DARK_THEME = "dark_theme"

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        ScreenModeHandler.setCurrentScreenMode(this)

    }
}
