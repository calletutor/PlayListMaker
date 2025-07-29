package com.example.playlistmaker.main.ui

import android.app.Application
import com.example.playlistmaker.favorites.di.dbDataModule
import com.example.playlistmaker.favorites.di.viewModelModule
import com.example.playlistmaker.di.DataModule.dataModule
import com.example.playlistmaker.di.InteractorModule.interactorModule
import com.example.playlistmaker.di.NavigationModule.navigationModule
import com.example.playlistmaker.di.RepositoryModule.repositoryModule
import com.example.playlistmaker.di.ViewModelSelectedTracksModule.mediaModule
import com.example.playlistmaker.playlists.di.playListDatabaseModule
import com.example.playlistmaker.playlists.di.playlistModule
import com.example.playlistmaker.settings.domain.ScreenModeHandler
import com.example.playlistmaker.settings.domain.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

const val DARK_THEME = "dark_theme"
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
                mediaModule,
                playlistModule,
                playListDatabaseModule
            )
        }

        val settingsInteractor = getKoin().get<SettingsInteractor>()
        val screenModeHandler = ScreenModeHandler(settingsInteractor)

        screenModeHandler.setCurrentScreenMode()

    }
}
