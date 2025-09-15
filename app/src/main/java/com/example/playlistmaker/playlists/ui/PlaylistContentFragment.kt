package com.example.playlistmaker.playlists.ui

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.BottomSheetPlaylistContentMenuBinding
import com.example.playlistmaker.databinding.BottomSheetPlaylistContentTrackListBinding
import com.example.playlistmaker.databinding.PlaylistContentFragmentBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.search.domain.Track
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.roundToInt

class PlaylistContentFragment : Fragment() {

    private var trackCountWord: String = ""

    private lateinit var adapter: TrackAdapter
    private val viewModel: PlaylistContentViewModel by viewModel()
    private val args: PlaylistContentFragmentArgs by navArgs()
    private var _binding: PlaylistContentFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val playlistId = args.playlistId
        viewModel.loadPlaylist(playlistId)

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->

            playlist?.coverImagePath?.let { uriString ->
                val uri = android.net.Uri.parse(uriString)
                binding.imageView.setImageURI(uri)
            } ?: run {
                binding.imageView.setImageResource(R.drawable.place_holder2)
            }

            binding.playlistName.text = playlist?.name

            binding.playlistDescription.text = playlist?.description

            val description = playlist?.description
            if (description.isNullOrBlank()) {
                binding.playlistDescription.isVisible = false
            } else {
                binding.playlistDescription.isVisible = true
                binding.playlistDescription.text = description
            }

            if (playlist?.tracksCount == 0) {
                binding.playlistDuration.text = getString(R.string.playlist_doesnt_contain_tracks)
                binding.tracksNumber.isVisible = false
                binding.icEllipse.isVisible = false
                binding.tracksNumber.isVisible = false
            } else {

                viewModel.totalDuration.observe(viewLifecycleOwner) { durationMillis ->
                    val minutes = (durationMillis / 60000.0).roundToInt()
                    binding.playlistDuration.text = "$minutes мин"
                }
            }
        }
        adapter = TrackAdapter(
            clickListener = TrackAdapter.TrackClickListener { track ->
                openPlayerScreen(track)
                Log.d("TrackClick", "Clicked on: ${track.trackName}")
            },
            longClickListener = TrackAdapter.TrackClickListener { track ->
                showRemoveTrackDialog(track, playlistId)
            }
        )

        binding.tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.tracksRecyclerView.adapter = adapter

        viewModel.tracks.observe(viewLifecycleOwner) { tracks ->
            adapter.tracks.clear()
            adapter.tracks.addAll(tracks)
            adapter.notifyDataSetChanged()

        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        viewModel.trackCountWord.observe(viewLifecycleOwner) { trackCountWord ->
            this.trackCountWord = trackCountWord
        }

        viewModel.trackCount.observe(viewLifecycleOwner) { trackCount ->
            binding.tracksNumber.text = trackCount
        }

        binding.icShare.setOnClickListener {
            share()
        }

        binding.icMenu.setOnClickListener {

            showBottomSheetMenu()

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistContentFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun openPlayerScreen(track: Track) {

        val action =
            PlaylistContentFragmentDirections.actionPlaylistContentFragmentToPlayerFragment(track)
        findNavController().navigate(action)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showRemoveTrackDialog(track: Track, playlistId: Long) {
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.track_to_be_deleted))
            .setMessage(getString(R.string.shall_the_track_be_deleted))
            .setNegativeButton(getString(R.string.NO)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .setPositiveButton(getString(R.string.YES)) { dialogInterface, _ ->
                viewModel.removeTrackFromPlaylist(track, playlistId)
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()
    }

    private fun showBottomSheetMenu() {

        val bottomSheetBinding = BottomSheetPlaylistContentMenuBinding.inflate(layoutInflater)

        val dialog = BottomSheetDialog(requireContext())

        dialog.setContentView(bottomSheetBinding.root)

        bottomSheetBinding.btnShare.setOnClickListener {
            dialog.dismiss()
            share()
        }

        bottomSheetBinding.btnEdit.setOnClickListener {
            dialog.dismiss()
            val action = PlaylistContentFragmentDirections
                .actionPlaylistContentFragmentToNewPlaylistFragment(args.playlistId)
            findNavController().navigate(action)
        }

        bottomSheetBinding.btnDelete.setOnClickListener {

            dialog.dismiss()

            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.playlist_deleting))
                .setMessage(getString(R.string.sure_to_delete_the_playlist))
                .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton(
                    getString(R.string.proceed_deleting)
                ) { dialogInterface, _ ->

                    viewModel.playlist.value?.let { playlist ->
                        viewModel.deletePlaylist(playlist)
                        findNavController().navigateUp()
                    }

                    findNavController().navigateUp()

                    dialogInterface.dismiss()
                }
                .create()
                .show()

        }

        dialog.show()
    }

    private fun share() {

        val playlist = viewModel.playlist.value
        val tracks = viewModel.tracks.value

        if (tracks.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_no_tracks_to_share),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val playlistName = playlist?.name ?: getString(R.string.no_name)
        val playlistDescription = playlist?.description ?: getString(R.string.no_description)
        val trackCount = tracks.size

        val trackListText = tracks.mapIndexed { index, track ->
            "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})"
        }.joinToString("\n")

        val shareText = buildString {
            appendLine(getString(R.string.playlist, playlistName))
            appendLine(getString(R.string.description2, playlistDescription))
            appendLine(getString(R.string.tracks_count, trackCount.toString()))
            appendLine()
            append(trackListText)
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, getString(R.string.share_the_playlist))
        startActivity(shareIntent)

    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.hideBottomNav()
    }
}
