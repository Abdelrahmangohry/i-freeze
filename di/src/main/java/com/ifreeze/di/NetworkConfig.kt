package com.ifreeze.di


import android.util.Log
import com.patient.data.cashe.PreferencesGateway
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor(preference: PreferencesGateway) {


        var baseUrl: String = "http://192.168.1.250:8443/api/"


    init {
        val savedBaseUrl = preference.loadBaseUrl()
        if (!savedBaseUrl.isNullOrEmpty()) {
            baseUrl = savedBaseUrl
            Log.d("server", "updatedUrl from server $baseUrl")
        }
    }
}

