package com.example.playlistmaker.playlists.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.NewPlaylistFragmentBinding
import java.io.File
import java.io.FileOutputStream
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlayListFragment : Fragment() {

    private var _binding: NewPlaylistFragmentBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri = result.data?.data

            imageUri?.let {
                selectedImageUri = it
                binding.playlistImage.setImageURI(it)
            }


        }
    }

    private var isSaved = false
    private val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = NewPlaylistFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (hasUnsavedChanges()) {
                        showExitConfirmationDialog()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }
        )

        binding.toolbar.setNavigationOnClickListener {
            if (hasUnsavedChanges()) {
                showExitConfirmationDialog()
            } else {
                findNavController().popBackStack()
            }
        }



        binding.toolbar.setNavigationOnClickListener {

            if (hasUnsavedChanges()) {
                showExitConfirmationDialog()
            } else {
                findNavController().popBackStack()
            }

        }

        binding.newPlaylistName.addTextChangedListener {
            viewModel.playlistName.value = it?.toString()
        }

        viewModel.isCreateButtonEnabled.observe(viewLifecycleOwner) { enabled ->
            binding.buttonCreatePlaylist.isEnabled = enabled
        }

        binding.buttonCreatePlaylist.setOnClickListener {

            val name = binding.newPlaylistName.text.toString()
            val description = binding.newPlaylistDescription.text.toString()

            viewModel.savePlaylist(
                name = name,
                description = description,
                imageUri = selectedImageUri
            )

        }

        binding.pictureSelectingButton.setOnClickListener {
            openImagePicker()
        }

        viewModel.successEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.onSuccessHandled()
                isSaved = true
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.onErrorHandled()
            }
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        selectImageLauncher.launch(intent)
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.unsaved_changes)
            .setMessage(R.string.sure_to_leave)
            .setPositiveButton(R.string.to_leave) { _, _ ->
                findNavController().popBackStack() // Закрыть фрагмент
            }
            .setNegativeButton(R.string.to_stay, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun hasUnsavedChanges(): Boolean {

        if (isSaved) return false

        val nameNotEmpty = binding.newPlaylistName.text?.isNotBlank() == true
        val descriptionNotEmpty = binding.newPlaylistDescription.text?.isNotBlank() == true
        val imageSelected = selectedImageUri != null

        return nameNotEmpty || descriptionNotEmpty || imageSelected
    }
}
