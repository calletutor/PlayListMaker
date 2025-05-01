package com.example.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.GONE
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.main.ui.CURRENT_TRACK_DATA
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.Track
import com.example.playlistmaker.player.ui.AudioPlayerActivity

class SearchActivity : AppCompatActivity() {

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var profressBar: ProgressBar
    private lateinit var searchFailedTextView: TextView
    private lateinit var badConnetctionImage: ImageView
    lateinit var searchFailedImage: ImageView

    //    private lateinit var searchFailedImage: ImageView
    private lateinit var refreshButton: Button
    lateinit var clearButton: ImageView
    private lateinit var clearHistoryButton: Button
    private lateinit var viewModel: SearchViewModel
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var inputEditText: EditText
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_activity)

        viewModel = ViewModelProvider(
            this,
            SearchViewModelFactory(
                Creator.provideTracksInteractor(),
                Creator.provideSearchHistoryInteractor()
            )
        ).get(SearchViewModel::class.java)

        initViews()
        initRecyclerView()
        initListeners()
        observeViewModel()

        // Восстановление состояния
        savedInstanceState?.getString(EDIT_TEXT)?.let {
            inputEditText.setText(it)
        }
    }

    private fun initViews() {

        profressBar = findViewById(R.id.progressBar)
        searchFailedImage = findViewById(R.id.searchFailedImage)
        clearButton = findViewById(R.id.clear_button)
        clearHistoryButton = findViewById(R.id.clearHistory)
        refreshButton = findViewById(R.id.refreshButton)
        inputEditText = findViewById(R.id.searchInputEditText)
        searchFailedTextView = findViewById(R.id.errorMessage)
        badConnetctionImage = findViewById(R.id.searchFailedImage)
        findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

    }

    private fun initRecyclerView() {
        val tracksList = findViewById<RecyclerView>(R.id.recyclerView)
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

        inputEditText.setOnFocusChangeListener { _, hasFocus ->

            if (hasFocus && inputEditText.text.isEmpty()) {

                viewModel.loadHasHistory()
                viewModel.hasHistory.observe(this) { hasHistory ->
                if (hasHistory) {
                    viewModel.loadSearchHistory()
                    clearHistoryButton.visibility = View.VISIBLE
                } else {
                    clearHistoryButton.visibility = GONE
                }

            }

            } else {
                clearHistoryButton.visibility = GONE
            }
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                viewModel.clearTracks() // очищаем список перед логикой

                if (s.isNullOrEmpty()) {

                    viewModel.loadSearchHistory()
                    clearButton.visibility = GONE
                    viewModel.cancelSearchDebounce()

                    viewModel.loadSearchHistory()
                    viewModel.loadHasHistory()

                } else {
                    viewModel.searchDebounce(s.toString())
                    clearButton.visibility = View.VISIBLE
                    clearHistoryButton.visibility = GONE
                    badConnetctionImage.visibility = GONE
                    searchFailedTextView.visibility = GONE
                    refreshButton.visibility = GONE
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        refreshButton.setOnClickListener {
            badConnetctionImage.visibility = GONE
            refreshButton.visibility = GONE
            searchFailedTextView.visibility = GONE

            profressBar.visibility = View.VISIBLE
//            findViewById<ProgressBar>(R.id.progressBar).visibility = View.VISIBLE

            viewModel.retrySearch()
        }

        clearButton.setOnClickListener {

            viewModel.onClearButtonClicked()
            inputEditText.setText("")
//            inputEditText.clearFocus()
            clearButton.visibility = GONE
            viewModel.cancelSearchDebounce()
            hideKeyboard()
            viewModel.loadSearchHistory()
            viewModel.loadHasHistory()

        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearHistory()
            clearHistoryButton.visibility = GONE
        }

    }

    private fun observeViewModel() {

        viewModel.hasHistory.observe(this) { hasHistory ->
            if (hasHistory && inputEditText.text.isEmpty() && inputEditText.hasFocus()) {
                clearHistoryButton.visibility = View.VISIBLE
            } else {
                clearHistoryButton.visibility = GONE
            }
        }

        viewModel.tracks.observe(this) { tracks ->
            tracksAdapter.tracks = tracks.toMutableList()
            tracksAdapter.notifyDataSetChanged()

            if (tracks.isNotEmpty()) {
                searchFailedTextView.visibility = GONE
                badConnetctionImage.visibility = GONE
                refreshButton.visibility = GONE
            }
        }

        viewModel.loading.observe(this) { isLoading ->
            profressBar.visibility = if (isLoading) View.VISIBLE else GONE
//            findViewById<ProgressBar>(R.id.progressBar).visibility = if (isLoading) View.VISIBLE else GONE
        }

        viewModel.error.observe(this) { errorMessage ->
            if (errorMessage != null) {
                when {
                    errorMessage == "Ничего не найдено." -> {
                        clearHistoryButton.visibility = GONE
                        showNotFoundMessage()
                    }

                    errorMessage == "Проблема с сетью." -> {
                        showNetworkError()
                    }

                    else -> {
                        showError(errorMessage)
                    }
                }
            } else {
                badConnetctionImage.visibility = GONE
                searchFailedTextView.visibility = GONE
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
        val intent = Intent(this, AudioPlayerActivity::class.java)
        intent.putExtra(CURRENT_TRACK_DATA, track)
        startActivity(intent)
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, inputEditText.text.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    private fun showNotFoundMessage() {
        searchFailedTextView.text = "Ничего не найдено."
        searchFailedImage.setImageResource(R.drawable.ic_not_found)
        badConnetctionImage.visibility = View.VISIBLE
        searchFailedTextView.visibility = View.VISIBLE
    }

    private fun showNetworkError() {

        searchFailedTextView.text = "Проблема с сетью."
        badConnetctionImage.setImageResource(R.drawable.ic_no_connecton)
        badConnetctionImage.visibility = View.VISIBLE
        searchFailedTextView.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
    }

    private fun hideKeyboard() {

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)

    }
}
