package com.example.playlistmaker

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.appcompat.app.AppCompatDelegate

class ScreenModeHandler(private val context: Context) {

    companion object {
        fun switchTheme(darkThemeEnabled: Boolean) {
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeEnabled) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }

        fun checkCurrentScreenMode(context: Context): Int {

            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            val currentMode = uiModeManager.currentModeType

            if (currentMode == Configuration.UI_MODE_TYPE_NORMAL) {
                // Проверяем, темная ли тема
                if ((context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                    // Темная тема активна
                    return 0
                } else {
                    // Светлая тема активна
                    return 1
                }
            }
            return -1
        }

        fun setCurrentScreenMode(context: Context) {

            val darkTheme: Boolean?
            val sharedPrefs = context.getSharedPreferences(DARK_THEME, MODE_PRIVATE)

            if (!sharedPrefs.getString(DARK_THEME, "").isNullOrEmpty()) {
                //сведения по теме экрана были ранее сохранены пользователем
                if (sharedPrefs.getString(DARK_THEME, "") == "true") {
                    darkTheme = true
                } else {
                    darkTheme = false
                }
                switchTheme(darkTheme)
            } else {
                //сведения по теме экрана ранее не были еще сохранены пользователем
                //do nothing
            }
        }
    }
}
