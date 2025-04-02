package com.example.playlistmaker

import android.app.Application

const val DARK_THEME = "dark_theme"
const val CURRENT_TRACK_DATA = "currentTrackData"
const val TRACK_HISTORY = "tracksHistory"

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        ScreenModeHandler.setCurrentScreenMode(this)

    }
}
