package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
    private val appLinkProvider: AppLinkProvider
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(appLinkProvider.getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(appLinkProvider.getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(appLinkProvider.getSupportEmailData())
    }
}
