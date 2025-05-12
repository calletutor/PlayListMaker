package com.example.playlistmaker.player.ui

import com.example.playlistmaker.R

data class PlayerScreenState(
    val isPrepared: Boolean = false,
    val isPlaying: Boolean = false,
    val playTime: String = "00:00",
    val buttonResId: Int = R.drawable.play_button
)
