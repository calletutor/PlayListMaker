package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchFragmentBinding
import com.example.playlistmaker.main.ui.MainActivity
import com.example.playlistmaker.search.domain.Track
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModel()

    private var _binding: SearchFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var tracksAdapter: TracksAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SearchFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initListeners()
        observeViewModel()


        savedInstanceState?.getString(EDIT_TEXT)?.let {
            binding.searchInputEditText.setText(it)
        }


        view.post {
            binding.searchInputEditText.setText("")
        }
    }

    private fun initRecyclerView() {
        tracksAdapter = TracksAdapter { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                openPlayerScreen(track)
            }
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = tracksAdapter
    }

    private fun initListeners() {
        binding.searchInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInputEditText.text.isEmpty()) {
                viewModel.loadSearchHistory()

                showKeyboard(binding.searchInputEditText)

            } else {
                binding.clearHistory.isVisible = false
            }
        }

        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.clearTracks()
                tracksAdapter.tracks = mutableListOf()
                tracksAdapter.notifyDataSetChanged()
                viewModel.cancelSearchDebounce()
                binding.progressBar.isVisible = false
                binding.searchFailedImage.isVisible = false
                binding.errorMessage.isVisible = false

                if (binding.searchInputEditText.hasFocus() && s.isNullOrEmpty()) {
                    viewModel.loadSearchHistory()
                    binding.clearButton.isVisible = false
                } else {
                    viewModel.searchDebounce(s.toString())
                    binding.clearButton.isVisible = true
                    binding.clearHistory.isVisible = false
                    binding.refreshButton.isVisible = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.refreshButton.setOnClickListener {
            binding.searchFailedImage.isVisible = false
            binding.refreshButton.isVisible = false
            binding.errorMessage.isVisible = false
            binding.progressBar.isVisible = true
            viewModel.retrySearch()
        }

        binding.clearButton.setOnClickListener {
            binding.searchInputEditText.setText("")
        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearTracks()
            tracksAdapter.tracks = mutableListOf()
            tracksAdapter.notifyDataSetChanged()
            viewModel.clearHistory()
            binding.clearHistory.isVisible = false
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            binding.clearHistory.isVisible =
                state.hasHistory && binding.searchInputEditText.text.isEmpty() && binding.searchInputEditText.hasFocus()

            binding.searchHistoryTitle.isVisible =
                state.hasHistory && binding.searchInputEditText.text.isEmpty() && binding.searchInputEditText.hasFocus()

            if (state.tracks.isNotEmpty()) {
                binding.errorMessage.isVisible = false
                binding.searchFailedImage.isVisible = false
                binding.refreshButton.isVisible = false

                tracksAdapter.tracks = state.tracks.toMutableList()
                tracksAdapter.notifyDataSetChanged()
                hideKeyboard()
            }

            binding.progressBar.isVisible = state.isLoading

            state.errorMessage?.let { errorMessage ->
                when (errorMessage) {
                    "Ничего не найдено." -> showNotFoundMessage()
                    "Проблема с сетью." -> showNetworkError()
                    else -> showError(errorMessage)
                }
            } ?: run {
                binding.searchFailedImage.isVisible = false
                binding.errorMessage.isVisible = false
            }
        }
    }

    private val clickRunnable = Runnable { isClickAllowed = true }

    private fun clickDebounce(): Boolean {
        val allowed = isClickAllowed
        if (allowed) {
            isClickAllowed = false
            handler.postDelayed(clickRunnable, CLICK_DEBOUNCE_DELAY)
        }
        return allowed
    }

    private fun openPlayerScreen(track: Track) {

        val action = SearchFragmentDirections.actionSearchFragmentToPlayerFragment(track)
        findNavController().navigate(action)

    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showNotFoundMessage() {
        binding.errorMessage.text = "Ничего не найдено."
        binding.searchFailedImage.setImageResource(R.drawable.ic_not_found)
        binding.searchFailedImage.isVisible = true
        binding.errorMessage.isVisible = true
    }

    private fun showNetworkError() {
        binding.errorMessage.text = "Проблема с сетью."
        binding.searchFailedImage.setImageResource(R.drawable.ic_no_connecton)
        binding.searchFailedImage.isVisible = true
        binding.errorMessage.isVisible = true
        binding.refreshButton.isVisible = true
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchInputEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _binding?.let {
            outState.putString(EDIT_TEXT, it.searchInputEditText.text.toString())
        }
    }

    override fun onResume() {
        super.onResume()
        isClickAllowed = true
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.clearTracks()
        tracksAdapter.tracks = mutableListOf()
        tracksAdapter.notifyDataSetChanged()

        binding.searchInputEditText.clearFocus()

        handler.removeCallbacks(clickRunnable)
        _binding = null

    }


    private fun showKeyboard(editText: EditText) {

        editText.post {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
