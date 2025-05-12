package com.example.playlistmaker.settings.ui

sealed class SettingsEvent {
    object ShareApp : SettingsEvent()
    object Support : SettingsEvent()
    object OpenAgreement : SettingsEvent()
}
