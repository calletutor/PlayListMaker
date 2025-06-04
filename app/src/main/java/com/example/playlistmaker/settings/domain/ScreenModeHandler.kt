package com.example.playlistmaker.settings.domain

class ScreenModeHandler(
    private val settingsInteractor: SettingsInteractor
) {
    fun setCurrentScreenMode() {

        settingsInteractor.wasThemeSaved(object : SettingsInteractor.SavedThemeHistoryConsumer {
            override fun consume(isThemeHistory: Boolean) {
                if (isThemeHistory) {
                    settingsInteractor.darkThemeIsEnabled(object :
                        SettingsInteractor.DarkThemeConsumer {
                        override fun consume(darkThemeIsEnabled: Boolean) {
                            if (darkThemeIsEnabled) {
                                settingsInteractor.applyDarkTheme(true)
                            } else {
                                settingsInteractor.applyDarkTheme(false)
                            }
                        }
                    })
                }
            }
        })
    }
}
