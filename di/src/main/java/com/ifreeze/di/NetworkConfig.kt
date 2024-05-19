package com.ifreeze.di


import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkConfig @Inject constructor() {
    var baseUrl: String = "https://security.flothers.com:8443/"
}