package com.example.playlistmaker.playlists.domain

import android.net.Uri

interface ImageStorageManager {
    fun copyImageToInternalStorage(uri: Uri): String?
}
