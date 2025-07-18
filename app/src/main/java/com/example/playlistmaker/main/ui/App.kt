package com.example.playlistmaker.main.ui

import android.app.Application
import com.example.playlistmaker.A_NEW.DI.dbDataModule
import com.example.playlistmaker.A_NEW.DI.viewModelModule
import com.example.playlistmaker.DI.DataModule.dataModule
import com.example.playlistmaker.DI.InteractorModule.interactorModule
import com.example.playlistmaker.DI.NavigationModule.navigationModule
import com.example.playlistmaker.DI.RepositoryModule.repositoryModule
import com.example.playlistmaker.DI.ViewModelSelectedTracksModule.mediaModule
import com.example.playlistmaker.settings.domain.ScreenModeHandler
import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val DARK_THEME = "dark_theme"
const val CURRENT_TRACK_DATA = "currentTrackData"
const val TRACK_HISTORY = "tracks_history"
const val SETTINGS_PREFS = "settings_prefs"

class App : Application() {

    override fun onCreate() {

        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                repositoryModule,
                interactorModule,
                navigationModule,
                dbDataModule,
                viewModelModule,
                mediaModule
            )
        }

        val settingsInteractor = getKoin().get<SettingsInteractor>()
        val screenModeHandler = ScreenModeHandler(settingsInteractor)

        screenModeHandler.setCurrentScreenMode()

    }
}
