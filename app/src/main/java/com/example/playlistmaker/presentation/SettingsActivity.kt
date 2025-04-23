package com.example.playlistmaker.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SettingsInteractor
import com.google.android.material.switchmaterial.SwitchMaterial
class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitcher: SwitchMaterial
    private val settingsInteractor: SettingsInteractor by lazy {
        Creator.provideSettingsInteractor()
    }

    override fun onResume() {
        super.onResume()

        // Узнаем, включена ли тёмная тема и ставим нужное состояние свитчера
        settingsInteractor.darkThemeIsEnabled(object : SettingsInteractor.DarkThemeConsumer {
            override fun consume(darkThemeIsEnabled: Boolean) {
                themeSwitcher.isChecked = darkThemeIsEnabled
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_activity)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupThemeSwitcher()
        setupOtherButtons()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupThemeSwitcher() {
        themeSwitcher = findViewById(R.id.themeSwitcher)

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            settingsInteractor.setDarkTheme(isChecked)
            settingsInteractor.applyDarkTheme(isChecked)
        }
    }

    private fun setupOtherButtons() {
        findViewById<TextView>(R.id.shareApp).setOnClickListener {
            val message = getString(R.string.offerPracticum)
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, message)
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        findViewById<TextView>(R.id.messageToSupport).setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.emailAddr)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.emailBody))
            }
            startActivity(emailIntent)
        }

        findViewById<TextView>(R.id.agreement).setOnClickListener {
            val link = getString(R.string.offerYandex)
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
        }
    }
}
