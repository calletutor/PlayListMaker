package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.SettingsFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class SettingsFragment : Fragment() {

    private val viewModel: SettingsViewModel by viewModel()

    private var _binding: SettingsFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SettingsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupListeners()
        observeViewModel()
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
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
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
        viewModel.loadInitialThemeState(requireContext())
    }

    override fun onDestroyView() {
        binding.themeSwitcher.setOnCheckedChangeListener(null)
        _binding = null
        super.onDestroyView()
    }
}
