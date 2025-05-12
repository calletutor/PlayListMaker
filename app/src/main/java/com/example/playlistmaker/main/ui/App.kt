package com.example.playlistmaker.main.ui

import android.app.Application
import com.example.playlistmaker.creator.domain.Creator
import com.example.playlistmaker.settings.domain.ScreenModeHandler

const val DARK_THEME = "dark_theme"
const val CURRENT_TRACK_DATA = "currentTrackData"
const val TRACK_HISTORY = "tracks_history"

class App : Application() {

    override fun onCreate() {

        super.onCreate()
        Creator.initApplication(this)

        val screenModeHandler = ScreenModeHandler(Creator.provideSettingsInteractor())
        screenModeHandler.setCurrentScreenMode()

    }
}
