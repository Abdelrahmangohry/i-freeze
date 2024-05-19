package com.ifreeze.di


import com.patient.data.cashe.PreferencesGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor(preference: PreferencesGateway) {


        var baseUrl: String = "https://security.flothers.com:8443/"


    init {
        val savedBaseUrl = preference.loadBaseUrl()
        if (!savedBaseUrl.isNullOrEmpty()) {
            baseUrl = savedBaseUrl
        }
    }
}

