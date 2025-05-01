package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {


    private val externalNavigator by lazy { Creator.provideExternalNavigator(this) }
    private val sharingInteractor by lazy {
        SharingInteractorImpl(
            externalNavigator,
            Creator.provideAppLinkProvider()
        )
    }

    private lateinit var themeSwitcher: SwitchMaterial
    private val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(
            Creator.provideSettingsInteractor(),
            Creator.provideSharingInteractor()
        )
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
        observeViewModel()
    }

    private fun setupToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupThemeSwitcher() {
        themeSwitcher = findViewById(R.id.themeSwitcher)

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onDarkThemeSwitched(isChecked)
        }
    }

    private fun setupOtherButtons() {
        findViewById<TextView>(R.id.shareApp).setOnClickListener {
            viewModel.onShareAppClicked()
        }

        findViewById<TextView>(R.id.messageToSupport).setOnClickListener {
            viewModel.onSupportClicked()
        }

        findViewById<TextView>(R.id.agreement).setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }

    private fun observeViewModel() {

        viewModel.darkThemeEnabled.observe(this) { isEnabled ->
            // Сначала отключаем слушатель
            themeSwitcher.setOnCheckedChangeListener(null)
            // Обновляем состояние Switch
            themeSwitcher.isChecked = isEnabled
            // Снова включаем слушатель
            themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onDarkThemeSwitched(isChecked)
            }
        }
    }
}
