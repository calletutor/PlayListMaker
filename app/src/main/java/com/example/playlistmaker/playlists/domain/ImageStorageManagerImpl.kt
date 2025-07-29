package com.example.playlistmaker.playlists.domain

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

class ImageStorageManagerImpl(
    private val context: Context
) : ImageStorageManager {


    companion object {
        private const val FILE_NAME_TEMPLATE = "playlist_image_%d.jpg"
    }

    override fun copyImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = FILE_NAME_TEMPLATE.format(System.currentTimeMillis())
//            val fileName = "playlist_image_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
