package com.ifreeze.di

import android.util.Log
import com.patient.data.cashe.PreferencesGateway
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class DynamicBaseUrlInterceptor(private val preferencesGateway: PreferencesGateway) : Interceptor {
    @Volatile private var baseUrl: HttpUrl? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        val baseUrlString = "https://central.flothers.com:8443/api/"
//        val baseUrlString = preferencesGateway.loadBaseUrl()
        if (baseUrlString != null) {
            baseUrl = baseUrlString.toHttpUrlOrNull()
        }

        if (baseUrl != null) {
            val newUrl = originalRequest.url.newBuilder()
                .scheme(baseUrl!!.scheme)
                .host(baseUrl!!.host)
                .port(baseUrl!!.port)
                .build()

            val newRequest = requestBuilder.url(newUrl).build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(originalRequest)
    }
}
