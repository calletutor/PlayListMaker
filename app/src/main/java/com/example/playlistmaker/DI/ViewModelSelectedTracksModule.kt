package com.example.playlistmaker.DI

import com.example.playlistmaker.media.ui.ViewModelSelectedTracks
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelSelectedTracksModule{
    val mediaModule = module {
        viewModel { ViewModelSelectedTracks(get()) }
    }
}
