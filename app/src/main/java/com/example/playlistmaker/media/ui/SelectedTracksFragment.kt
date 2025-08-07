package com.example.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.SelectedTracksFragmentBinding
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel


class SelectedTracksFragment : Fragment() {

    private val viewModel: SelectedTracksViewModel by viewModel()
    private var _binding: SelectedTracksFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: TrackAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = SelectedTracksFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = TrackAdapter(

            TrackAdapter.TrackClickListener { track ->
                val action = MediaFragmentDirections.actionMediaFragmentToPlayerFragment(track)
                NavHostFragment.findNavController(requireParentFragment()).navigate(action)
            },

            longClickListener = TrackAdapter.TrackClickListener { track ->
                val stop = 1
                // Пока ничего, или покажи диалог, или сделай лог:
//                Toast.makeText(requireContext(), "Long clicked: ${track.title}", Toast.LENGTH_SHORT).show()
            }

        )

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        setupObservers()

    }

    private fun setupObservers() {

        viewModel.favoriteTracks.observe(viewLifecycleOwner) { trackList ->

            adapter.tracks = trackList.toMutableList()
            adapter.notifyDataSetChanged()

            if (trackList.isEmpty()) {
                binding.recyclerView.visibility = View.GONE
                binding.noResultsPlaceholder.visibility = View.VISIBLE
                binding.errorMessage.visibility = View.VISIBLE
            } else {
                binding.recyclerView.visibility = View.VISIBLE
                binding.noResultsPlaceholder.visibility = View.GONE
                binding.errorMessage.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recyclerView.adapter = null
        _binding = null
    }
}
