package com.example.playlistmaker.player.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
//import com.example.playlistmaker.Manifest
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.BottomSheetLayoutBinding
import com.example.playlistmaker.databinding.PlayerFragmentBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.player.domain.MusicService
import com.example.playlistmaker.playlists.domain.StringProviderImpl
import com.example.playlistmaker.playlists.ui.PlaylistAdapterCompact
//import com.example.playlistmaker.playlists.ui.PlaylistAdapter
//import com.example.playlistmaker.playlists.ui.PlaylistAdapterMode
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Locale
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayerFragment : Fragment() {

    private lateinit var stringProvider: StringProviderImpl
    private val viewModel: PlayerViewModel by viewModel()

    private var _binding: PlayerFragmentBinding? = null
    private val binding get() = _binding!!

    private val currentTrack: Track by lazy {
        PlayerFragmentArgs.fromBundle(requireArguments()).track
    }

    private var isBound = false

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            val controller = binder.getController() // <- теперь только интерфейс
            isBound = true
            viewModel.bindService(controller)
            viewModel.preparePlayer(currentTrack)     // <- этот вызов остаётся
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            viewModel.unbindService()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlayerFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stringProvider = StringProviderImpl(requireContext())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? MainActivity)?.setBottomNavVisible(false)
        bindTrackInfoToUI(currentTrack)
        setupObservers()
        setupClickListeners()

        val intent = Intent(requireContext(), MusicService::class.java)
        requireContext().bindService(intent, connection, Context.BIND_AUTO_CREATE)

//        viewModel.preparePlayer(currentTrack)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            viewModel.releasePlayer()
            requireContext().unbindService(connection)
            isBound = false
        }
        (activity as? MainActivity)?.setBottomNavVisible(true)
        _binding = null
    }


    private fun setupObservers() {

        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.playbackButton.setPlaying(state.isPlaying)
            binding.playingTime.text = state.playTime

            val iconRes = if (state.isFavorite) {
                R.drawable.favorites_button_selected
            } else {
                R.drawable.favorites_button_unselected
            }
            binding.favoritesImage.setImageResource(iconRes)
        }

        viewModel.toastMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearToastMessage()
            }
        }

        viewModel.successEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearSuccessMessage()
            }
        }

        viewModel.errorEvent.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.clearErrorMessage()
            }
        }
    }

    private fun setupClickListeners() {

        binding.playbackButton.setOnPlaybackToggleListener {
            viewModel.playbackControl()
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.favoritesImage.setOnClickListener {
            viewModel.toggleFavorite(currentTrack)
        }

        binding.addToPlaylistButton.setOnClickListener {
            viewModel.loadPlaylists()

            // Создаём BottomSheet с кастомным стилем, чтобы фон был сразу задан
            val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)

            // Используем ViewBinding для контента BottomSheet
            val bottomSheetBinding = BottomSheetLayoutBinding.inflate(layoutInflater)
            dialog.setContentView(bottomSheetBinding.root)

            bottomSheetBinding.createNewPlaylistButton.setOnClickListener {
                dialog.dismiss()
                findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
            }

            val adapter = PlaylistAdapterCompact(stringProvider).apply {
                setOnItemClickListener { playlist ->
                    currentTrack.trackId?.let {
                        viewModel.addTrackToPlaylist(playlist.playlistId)
                    }
                    dialog.dismiss()
                }
            }

            bottomSheetBinding.recyclerView.adapter = adapter
            bottomSheetBinding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

            viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
                adapter.submitList(playlists)
            }

            dialog.show()
        }
    }

    private fun bindTrackInfoToUI(track: Track) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName

        try {
            binding.trackTimeValue.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(track.trackTimeMillis.toLong())
        } catch (e: Exception) {
            binding.trackTimeValue.text = track.trackTimeMillis
        }

        binding.albumValue.text = track.collectionName
        binding.yearValue.text = track.releaseDate.take(4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8f, requireContext())))
            .placeholder(R.drawable.place_holder)
            .into(binding.imageView)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }


    override fun onStart() {
        super.onStart()
        checkAndRequestNotificationPermission()
        viewModel.onUIStart()  // скрываем notification

    }

    override fun onStop() {
        super.onStop()

        viewModel.onUIStop()   // показываем notification

    }



    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = "android.permission.POST_NOTIFICATIONS"
            if (ContextCompat.checkSelfPermission(requireContext(), permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(permission), 1001)
            }
        }
    }



//    private fun checkAndRequestNotificationPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    requireContext(),
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
//            }
//        }
//    }




}
