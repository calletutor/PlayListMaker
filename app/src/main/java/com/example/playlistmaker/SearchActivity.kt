package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(ItunesApi::class.java)

    var trackList: MutableList<Track> = mutableListOf()
    var tracksAdapter = TracksAdapter(trackList)
    lateinit var clearHistoryButton: Button
    lateinit var searchHistoryTitle: TextView
    lateinit var clearButton: ImageView
    lateinit var searchFailedImage: ImageView
    lateinit var refreshButton: Button
    lateinit var searchFailedTextView: TextView
    private lateinit var inputEditText: EditText
    private var currentText: String? = null
    lateinit var recyclerView:RecyclerView

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        clearHistoryButton = findViewById(R.id.clearHistory)
        searchHistoryTitle = findViewById(R.id.searchHistoryTitle)
        inputEditText = findViewById(R.id.searchInputEditText)
        clearButton = findViewById(R.id.clear_button)
        searchFailedImage = findViewById(R.id.searchFailedImage)
        refreshButton = findViewById(R.id.refreshButton)
        searchFailedTextView = findViewById(R.id.errorMessage)
        refreshButton.visibility = View.INVISIBLE
        searchFailedTextView.visibility = View.INVISIBLE

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tracksAdapter = TracksAdapter(trackList)
        recyclerView.adapter = tracksAdapter

        lateinit var sharedPreferences: SharedPreferences
        lateinit var trackListType: Type
        var jsonString: String?

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {

                sharedPreferences = getSharedPreferences("tracksHistory", Context.MODE_PRIVATE)
                jsonString = sharedPreferences.getString("tracksHistory", null)

                if (!jsonString.isNullOrEmpty()) {

                    trackListType = object : TypeToken<MutableList<Track>>() {}.type
                    trackList = Gson().fromJson(jsonString, trackListType)

                    tracksAdapter = TracksAdapter(trackList)
                    recyclerView.adapter = tracksAdapter

                    clearHistoryButton.visibility = View.VISIBLE
                    searchHistoryTitle.visibility = View.VISIBLE

                }
            }
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                trackList.clear()
                tracksAdapter.notifyDataSetChanged()

                searchHistoryTitle.visibility = View.INVISIBLE
                clearHistoryButton.visibility = View.INVISIBLE

                if (inputEditText.text.isNotEmpty()) {

                    searchAttempt()

                }
            }
            false
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable?) {

                if (inputEditText.text.isEmpty()) {

                    clearButton.visibility = View.INVISIBLE

                    sharedPreferences = getSharedPreferences("tracksHistory", Context.MODE_PRIVATE)
                    jsonString = sharedPreferences.getString("tracksHistory", null)

                    if (!jsonString.isNullOrEmpty()) {
                        trackListType = object : TypeToken<MutableList<Track>>() {}.type
                        trackList = Gson().fromJson(jsonString, trackListType)
                        tracksAdapter = TracksAdapter(trackList)
                        recyclerView.adapter = tracksAdapter
                        //tracksAdapter.notifyDataSetChanged()
                        clearHistoryButton.visibility = View.VISIBLE
                        searchHistoryTitle.visibility = View.VISIBLE
                    }

                } else {

                    trackList.clear()
                    tracksAdapter = TracksAdapter(trackList)
                    recyclerView.adapter = tracksAdapter
                    //tracksAdapter.notifyDataSetChanged()
                    clearHistoryButton.visibility = View.INVISIBLE
                    searchHistoryTitle.visibility = View.INVISIBLE

                    searchFailedImage.visibility = View.INVISIBLE
                    searchFailedTextView.visibility = View.INVISIBLE
                    refreshButton.visibility = View.INVISIBLE

                    if (s.toString().isEmpty()) {
                        clearButton.isVisible = false
                    } else {
                        clearButton.isVisible = true
                        currentText = inputEditText.text.toString()
                    }
                }
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        clearHistoryButton.setOnClickListener {
            trackList.clear()
            //tracksAdapter.notifyDataSetChanged()
            tracksAdapter = TracksAdapter(trackList)
            recyclerView.adapter = tracksAdapter

            sharedPreferences.edit()
                .clear()
                .apply()

            clearHistoryButton.visibility = View.INVISIBLE
            searchHistoryTitle.visibility = View.INVISIBLE

        }

        refreshButton.setOnClickListener {

            searchFailedImage.visibility = View.INVISIBLE
            searchFailedTextView.visibility = View.INVISIBLE
            refreshButton.visibility = View.INVISIBLE

            if (inputEditText.text.isNotEmpty()) {

                searchAttempt()

            }
        }

        clearButton.setOnClickListener {

            trackList.clear()
            tracksAdapter.notifyDataSetChanged()
            searchFailedTextView.visibility = View.INVISIBLE
            searchFailedImage.visibility = View.INVISIBLE
            inputEditText.setText("")
            clearButton.visibility = View.INVISIBLE
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()

        }
    }

    fun searchAttempt() {

        imdbService.search(inputEditText.text.toString()).enqueue(object :
            Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.results?.isNotEmpty() == true) {

                        trackList.addAll(response.body()?.results!!)
                        tracksAdapter = TracksAdapter(trackList)
                        recyclerView.adapter = tracksAdapter

                        //tracksAdapter.notifyDataSetChanged()
                    }

                    if (trackList.isEmpty()) {
                        searchFailedTextView.setText(R.string.searchFailed)
                        searchFailedImage.setImageResource(R.drawable.ic_not_found)
                        searchFailedImage.visibility = View.VISIBLE
                        searchFailedTextView.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                searchFailedTextView.setText(R.string.connectionFaied)
                searchFailedImage.setImageResource(R.drawable.ic_no_connecton)
                searchFailedImage.visibility = View.VISIBLE
                searchFailedTextView.visibility = View.VISIBLE
                refreshButton.visibility = View.VISIBLE
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, currentText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentText = savedInstanceState.getString(EDIT_TEXT)
        inputEditText.setText(currentText)
    }
}
