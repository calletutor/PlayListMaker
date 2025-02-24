package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.settings_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val sharedPrefs = getSharedPreferences(DARK_THEME, MODE_PRIVATE)
        if (sharedPrefs.getString(DARK_THEME, "") == "true") {
            themeSwitcher.isChecked=true
            //darkTheme = true
            //(applicationContext as App).switchTheme(true)
        } else {
            themeSwitcher.isChecked=false
            //darkTheme = false
            //(applicationContext as App).switchTheme(false)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->

        }

        val share = findViewById<TextView>(R.id.shareApp)
        val getSupport = findViewById<TextView>(R.id.messageToSupport)
        val userAgreement = findViewById<TextView>(R.id.agreement)

        share.setOnClickListener {
            val message = getString(R.string.offerPracticum)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(shareIntent, null))
        }

        getSupport.setOnClickListener {
            val email = getString(R.string.emailAddr)
            val subject = getString(R.string.emailSubject)
            val body = getString(R.string.emailBody)
            val emailIntent = Intent(Intent.ACTION_SENDTO)

            emailIntent.apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
            }

            startActivity(emailIntent)

        }

        userAgreement.setOnClickListener {
            val link = getString(R.string.offerYandex)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            val editor = sharedPrefs.edit()
            if (checked){
                editor.putString(DARK_THEME, "true")
            }else{
                editor.putString(DARK_THEME, "false")
            }
            editor.apply()
        }
    }
}
