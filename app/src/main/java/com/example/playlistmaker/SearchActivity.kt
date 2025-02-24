package com.example.playlistmaker

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val imdbService = retrofit.create(ItunesApi::class.java)

    var trackList: MutableList<Track> = mutableListOf()

    var tracksAdapter = TracksAdapter(trackList)

    lateinit var clearButton:ImageView
    lateinit var errorImage:ImageView
    lateinit var refreshButton:Button
    lateinit var errorTextView:TextView
    private lateinit var inputEditText: EditText

    private var currentText: String? = null

    companion object {
        const val EDIT_TEXT = "EDIT_TEXT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.search_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        inputEditText = findViewById(R.id.searchInputEditText)
        clearButton = findViewById<ImageView>(R.id.clear_button)
        errorImage = findViewById<ImageView>(R.id.errorImage)
        refreshButton = findViewById<Button>(R.id.refreshButton)
        errorTextView = findViewById<TextView>(R.id.errorMessage)

        refreshButton.visibility = View.INVISIBLE
        errorTextView.visibility = View.INVISIBLE

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        tracksAdapter = TracksAdapter(trackList)
        recyclerView.adapter = tracksAdapter

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {

                trackList.clear()
                tracksAdapter.notifyDataSetChanged()

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

                errorImage.visibility = View.INVISIBLE
                refreshButton.visibility = View.INVISIBLE

                if (s.toString().isEmpty()) {
                    clearButton.isVisible = false
                } else {
                    clearButton.isVisible = true
                    currentText = inputEditText.text.toString()
                }
            }
        }

        inputEditText.addTextChangedListener(textWatcher)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        refreshButton.setOnClickListener {

            errorImage.visibility = View.INVISIBLE
            errorTextView.visibility = View.INVISIBLE
            refreshButton.visibility = View.INVISIBLE

            if (inputEditText.text.isNotEmpty()) {

                searchAttempt()

            }
        }

        clearButton.setOnClickListener {

            trackList.clear()
            tracksAdapter.notifyDataSetChanged()
            errorTextView.visibility = View.INVISIBLE
            inputEditText.setText("")
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()

        }
    }

    fun searchAttempt (){

        imdbService.search(inputEditText.text.toString()).enqueue(object :
            Callback<TracksResponse> {
            override fun onResponse(
                call: Call<TracksResponse>,
                response: Response<TracksResponse>
            ) {
                if (response.code() == 200) {

                    if (response.body()?.results?.isNotEmpty() == true) {
                        trackList.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
                    }

                    if (trackList.isEmpty()) {
                        errorTextView.setText(R.string.searchFailed)
                        errorImage.setImageResource(R.drawable.ic_not_found)
                        errorImage.visibility = View.VISIBLE
                        errorTextView.visibility = View.VISIBLE
                    } else {
                        //true
                    }
                } else {
                    //true
                }
            }

            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                errorTextView.setText(R.string.connectionFaied)
                errorImage.setImageResource(R.drawable.ic_no_connecton)
                errorImage.visibility = View.VISIBLE
                errorTextView.visibility = View.VISIBLE
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
