package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.main.ui.CURRENT_TRACK_DATA
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.SearchActivityBinding
import com.example.playlistmaker.search.domain.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private val viewModel: SearchViewModel by viewModel()

    private lateinit var binding: SearchActivityBinding

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var tracksAdapter: TracksAdapter

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        initRecyclerView()
        initListeners()
        observeViewModel()

        // Восстановление состояния
        savedInstanceState?.getString(EDIT_TEXT)?.let {
            binding.searchInputEditText.setText(it)
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initRecyclerView() {

        val tracksList = binding.recyclerView

        tracksAdapter = TracksAdapter { track ->
            if (clickDebounce()) {
                viewModel.saveTrackToHistory(track)
                openPlayerScreen(track)
            }
        }
        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = tracksAdapter
    }

    private fun initListeners() {
        binding.searchInputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.searchInputEditText.text.isEmpty()) {
                viewModel.loadSearchHistory() // Загружаем историю
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
                if (s.isNullOrEmpty()) {
                    //поле поиска пустое
                    viewModel.loadSearchHistory()
                    binding.clearButton.isVisible = false
                } else {
                    //поле поиска не пустое
                    //делаем поиск треков
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
        viewModel.uiState.observe(this) { state ->
            binding.clearHistory.isVisible =
                if (state.hasHistory && binding.searchInputEditText.text.isEmpty() && binding.searchInputEditText.hasFocus()) true else false

            binding.searchHistoryTitle.isVisible =
                if (state.hasHistory && binding.searchInputEditText.text.isEmpty() && binding.searchInputEditText.hasFocus()) true else false

            if (state.tracks.isNotEmpty()) {
                binding.errorMessage.isVisible = false
                binding.searchFailedImage.isVisible = false
                binding.refreshButton.isVisible = false

                tracksAdapter.tracks = state.tracks.toMutableList()
                tracksAdapter.notifyDataSetChanged()
                hideKeyboard()

            }

            binding.progressBar.isVisible = if (state.isLoading) true else false

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

    private fun clickDebounce(): Boolean {
        val allowed = isClickAllowed
        if (allowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return allowed
    }

    private fun openPlayerScreen(track: Track) {
        val intent = Intent(this, PlayerActivity::class.java)
        intent.putExtra(CURRENT_TRACK_DATA, track)
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.searchInputEditText.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, binding.searchInputEditText.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
