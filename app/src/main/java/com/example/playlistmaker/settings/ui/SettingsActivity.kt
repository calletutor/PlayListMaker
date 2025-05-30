package com.example.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.SettingsActivityBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel: SettingsViewModel by viewModel()

    private lateinit var binding: SettingsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupToolbar()
        setupListeners()
        observeViewModel()

    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupListeners() {

        binding.shareApp.setOnClickListener {
            viewModel.onShareAppClicked()
        }

        binding.messageToSupport.setOnClickListener {
            viewModel.onSupportClicked()
        }

        binding.agreement.setOnClickListener {
            viewModel.onAgreementClicked()
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            updateThemeSwitch(state.isDarkThemeEnabled)
        }
    }

    private fun updateThemeSwitch(isChecked: Boolean) {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        binding.themeSwitcher.isChecked = isChecked
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            if (binding.themeSwitcher.isPressed) {
                viewModel.onDarkThemeSwitched(checked)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadInitialThemeState(this)
    }

    override fun onDestroy() {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        super.onDestroy()
    }
}
