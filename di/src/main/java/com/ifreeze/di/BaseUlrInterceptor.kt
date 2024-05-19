package com.ifreeze.di

import okhttp3.Interceptor
import okhttp3.Response

class BaseUrlInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            chain.proceed(chain.request())
        } catch (e : UnknownError) {

            throw e // Re-throw the exception after handling it
        }
    }
}
