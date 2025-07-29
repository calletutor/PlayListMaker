package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.SelectedTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelSelectedTracksModule{
    val mediaModule = module {
        viewModel { SelectedTracksViewModel(get()) }
    }
}
