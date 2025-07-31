package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.databinding.PlaylistsFragmentBinding
import com.example.playlistmaker.playlists.domain.StringProviderImpl
import com.example.playlistmaker.playlists.ui.NewPlaylistViewModel
import com.example.playlistmaker.playlists.ui.PlaylistAdapter
import com.example.playlistmaker.playlists.ui.PlaylistAdapterMode
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistsFragment : Fragment() {

    private var _binding: PlaylistsFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PlaylistsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createNewPlaylistButton.setOnClickListener {
            val action = MediaFragmentDirections.actionMediaFragmentToFragmentNewPlayList()
            NavHostFragment.findNavController(requireParentFragment()).navigate(action)
        }

        val stringProvider = StringProviderImpl(requireContext())
        val adapter = PlaylistAdapter(stringProvider, PlaylistAdapterMode.DEFAULT)


        binding.playlistRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistRecyclerView.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isNullOrEmpty()) {
                binding.playlistRecyclerView.visibility = View.GONE
                binding.emptyStateLayout.visibility = View.VISIBLE
            } else {
                binding.playlistRecyclerView.visibility = View.VISIBLE
                binding.emptyStateLayout.visibility = View.GONE
                adapter.submitList(playlists)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
