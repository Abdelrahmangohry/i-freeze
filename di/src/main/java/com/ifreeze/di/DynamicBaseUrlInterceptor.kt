package com.ifreeze.di

import com.ifreeze.data.cash.PreferencesGateway
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * An OkHttp interceptor that dynamically changes the base URL of HTTP requests.
 * This interceptor allows for the base URL to be updated during runtime, based on a value
 * stored in shared preferences via the `PreferencesGateway`.
 *
 * @property preferencesGateway An instance of `PreferencesGateway` used to load the base URL.
 */
class DynamicBaseUrlInterceptor(private val preferencesGateway: PreferencesGateway) : Interceptor {
    // Volatile variable to ensure thread-safe access to the base URL.
    @Volatile private var baseUrl: HttpUrl? = null

    /**
     * Intercepts the HTTP request and dynamically changes the base URL if available.
     *
     * @param chain The request chain provided by OkHttp.
     * @return The HTTP response after potentially modifying the request's base URL.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // Load the base URL string from shared preferences via the PreferencesGateway.
        val baseUrlString = preferencesGateway.loadBaseUrl()
//        val baseUrlString = "https://security.flothers.com:8443/api/"

        // Convert the base URL string to an HttpUrl object, if valid.
        if (baseUrlString != null) {
            baseUrl = baseUrlString.toHttpUrlOrNull()
        }
        // If a valid base URL exists, modify the request's URL with the new base URL.
        if (baseUrl != null) {
            val newUrl = originalRequest.url.newBuilder()
                .scheme(baseUrl!!.scheme)
                .host(baseUrl!!.host)
                .port(baseUrl!!.port)
                .build()

            val newRequest = requestBuilder.url(newUrl).build()
            return chain.proceed(newRequest)
        }
        // If no base URL is provided, proceed with the original request.
        return chain.proceed(originalRequest)
    }
}
