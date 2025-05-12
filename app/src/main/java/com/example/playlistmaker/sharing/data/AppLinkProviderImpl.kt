package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.sharing.domain.model.EmailData

class AppLinkProviderImpl(private val context: Context) : AppLinkProvider {

    override fun getShareAppLink(): String {
        return context.getString(R.string.offerPracticum)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.offerYandex)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.emailAddr),
            subject = context.getString(R.string.emailSubject),
            body = context.getString(R.string.emailBody)
        )
    }
}
