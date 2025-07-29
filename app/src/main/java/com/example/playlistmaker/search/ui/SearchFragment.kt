package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchFragmentBinding
import com.example.playlistmaker.search.domain.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlinx.coroutines.flow.first

class SearchFragment : Fragment() {

    private var isHistory: Boolean = false

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

        binding.searchInputEditText.setText(viewModel.currentQuery)
        binding.clearButton.isVisible = !viewModel.currentQuery.isNullOrEmpty()

        isHistory = viewModel.uiState.value?.isShowingHistory ?: false

        if (isHistory) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadSearchHistory()
            }
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

                if (!isHistory) {
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewModel.loadSearchHistory()
                    }
                }


                showKeyboard(binding.searchInputEditText)
            }
        }

        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

                viewModel.currentQuery = s.toString()

                val currentInput = s?.toString().orEmpty()
                if (currentInput != viewModel.currentQuery) {
                    viewModel.clearErrorMessage()
                }


                if (binding.searchInputEditText.hasFocus()) {
                    viewModel.clearTracks()
                    binding.searchHistoryTitle.isVisible = false
                    binding.clearHistory.isVisible = false
                    binding.refreshButton.isVisible = false
                }


                if (binding.searchInputEditText.hasFocus()) {
                    if (s.isNullOrEmpty()) {

                        viewLifecycleOwner.lifecycleScope.launch {
                            viewModel.loadSearchHistory()
                        }

                        binding.clearButton.isVisible = false

                        viewModel.cancelSearchDebounce()

                    } else {

                        viewModel.clearTracks()

                        binding.searchFailedImage.isVisible = false
                        binding.errorMessage.isVisible = false
                        binding.refreshButton.isVisible = false

                        viewModel.cancelSearchDebounce()
                        binding.searchHistoryTitle.isVisible = false
                        binding.clearHistory.isVisible = false

                        viewModel.hideHistory()

                        viewModel.searchDebounce(s.toString())
                        binding.clearButton.isVisible = true
                    }
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
            binding.clearButton.isVisible = false

            viewModel.cancelSearchDebounce()
            viewModel.clearTracks()

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.loadSearchHistory()
            }

        }

        binding.clearHistory.setOnClickListener {
            viewModel.clearTracks()
            viewModel.clearHistory()

        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->

            binding.searchHistoryTitle.isVisible = state.isShowingHistory
            binding.clearHistory.isVisible = state.isShowingHistory

            if (state.tracks.isNotEmpty()) {

                tracksAdapter.tracks = state.tracks.toMutableList()
                binding.errorMessage.isVisible = false
                binding.searchFailedImage.isVisible = false
                binding.refreshButton.isVisible = false
                hideKeyboard()

            } else {
                tracksAdapter.tracks = mutableListOf()
            }

            tracksAdapter.notifyDataSetChanged()

            binding.progressBar.isVisible = state.isLoading

            state.error?.let { error ->

                val currentInput = binding.searchInputEditText.text.toString()

                if (state.lastQuery == currentInput) {
                    when (error) {
                        SearchError.NothingFound -> showNotFoundMessage()
                        SearchError.Network -> showNetworkError()
                        else -> showUnknownError()
                    }
                } else {
                    binding.errorMessage.isVisible = false
                    binding.searchFailedImage.isVisible = false
                    binding.refreshButton.isVisible = false
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

    private fun showNotFoundMessage() {
        binding.errorMessage.text = getString(R.string.nothing_found)
        binding.searchFailedImage.setImageResource(R.drawable.ic_not_found)
        binding.searchFailedImage.isVisible = true
        binding.errorMessage.isVisible = true
    }

    private fun showNetworkError() {
        binding.errorMessage.text = getString(R.string.connection_failed)
        binding.searchFailedImage.setImageResource(R.drawable.ic_no_connecton)
        binding.searchFailedImage.isVisible = true
        binding.errorMessage.isVisible = true
        binding.refreshButton.isVisible = true
    }

    private fun showUnknownError() {
        binding.errorMessage.text = getString(R.string.unknown_error)
        binding.searchFailedImage.isVisible = true
        binding.errorMessage.isVisible = true
        binding.refreshButton.isVisible = true
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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

        handler.removeCallbacks(clickRunnable)
        _binding = null

    }

    private fun showKeyboard(editText: EditText) {

        editText.post {
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}
