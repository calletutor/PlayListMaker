package com.example.playlistmaker.player.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlayerFragmentBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.playlists.domain.PlaylistRepository
import com.example.playlistmaker.playlists.domain.StringProviderImpl
import com.example.playlistmaker.playlists.ui.PlaylistAdapter
import com.example.playlistmaker.playlists.ui.PlaylistAdapterMode
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
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

        viewModel.preparePlayer(currentTrack)


    }

    private fun setupObservers() {


        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.playImage.setImageResource(state.buttonResId)
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

        binding.playImage.setOnClickListener {
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

            val dialog = BottomSheetDialog(requireContext()).apply {
                setContentView(R.layout.bottom_sheet_layout)

                val createButton = findViewById<Button>(R.id.create_new_playlist_button)
                createButton?.setOnClickListener {
                    dismiss()
                    findNavController().navigate(R.id.action_playerFragment_to_fragmentNewPlayList)
                }

                val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)!!
                val adapter = PlaylistAdapter(stringProvider, PlaylistAdapterMode.COMPACT).apply {
                    setOnItemClickListener { playlist ->

                        currentTrack.trackId?.let { trackId ->

                            viewModel.addTrackToPlaylist(playlist.playlistId)



                        }
                        dismiss()
                    }
                }
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
                    adapter.submitList(playlists)
                }
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

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? MainActivity)?.setBottomNavVisible(true)
        _binding = null
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }




}
