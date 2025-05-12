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

        // Обработка insets для edge-to-edge
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
        binding.toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        // Обработчик для темы

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            if (binding.themeSwitcher.isPressed) { // Проверяем, что изменение инициировано пользователем
                viewModel.onDarkThemeSwitched(isChecked)
            }
        }

        // Обработчики для других кнопок
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
            // Обновление переключателя темы
            updateThemeSwitch(state.isDarkThemeEnabled)
        }
    }

    private fun updateThemeSwitch(isChecked: Boolean) {
        // Временно отключаем слушатель, чтобы избежать рекурсии
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        binding.themeSwitcher.isChecked = isChecked
        binding.themeSwitcher.setOnCheckedChangeListener { _, checked ->
            if (binding.themeSwitcher.isPressed) {
                viewModel.onDarkThemeSwitched(checked)
            }
        }
    }

    override fun onDestroy() {
        // Очищаем слушатели
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        super.onDestroy()
    }
}
