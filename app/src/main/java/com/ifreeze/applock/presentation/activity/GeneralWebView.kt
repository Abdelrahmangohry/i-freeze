    package com.ifreeze.applock.presentation.activity

    import android.annotation.SuppressLint
    import android.util.Log
    import android.view.View
    import android.webkit.WebResourceRequest
    import android.webkit.WebView
    import android.webkit.WebViewClient
    import androidx.annotation.RequiresApi
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.platform.LocalContext
    import androidx.compose.ui.viewinterop.AndroidView
    import androidx.navigation.NavController
    import com.patient.data.cashe.PreferencesGateway


    @RequiresApi(34)
    @Composable
    fun GeneralWebView(navController: NavController) {
        val preference = PreferencesGateway(LocalContext.current)
        val isBlacklistedChecked = remember { mutableStateOf(preference.load("WebBlacklist", false)) }
        val isWhitelistedChecked = remember { mutableStateOf(preference.load("WebWhitelist", false)) }
        var blockedWebsites = preference.getList("blockedWebsites")
        var allowedWebsites = preference.getList("allowedWebsites")

        Log.d("abd", "isBlacklistedChecked $isBlacklistedChecked")
        Log.d("abd", "isWhitelistedChecked $isWhitelistedChecked")
        Log.d("abd", "blockedWebsites $blockedWebsites")
        Log.d("abd", "allowedWebsites $allowedWebsites")
        Log.d("abd", "isBlacklistedChecked.value ${isBlacklistedChecked.value}")
        Log.d("abd", "isWhitelistedChecked.value ${isWhitelistedChecked.value}")
        Log.d("abd", "blockedWebsites.any ${blockedWebsites.any()}")


        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            isBlacklistedChecked.value?.let {
                isWhitelistedChecked.value?.let { it1 ->
                    startWebView(
                        isBlacklistedChecked = it,
                        isWhitelistedChecked = it1,
                        blockedWebsites = blockedWebsites,
                        allowedWebsites = allowedWebsites
                    )
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun startWebView(
        isBlacklistedChecked: Boolean,
        isWhitelistedChecked: Boolean,
        blockedWebsites: List<String>,
        allowedWebsites: List<String>
    ) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    this.clearCache(true)
                    this.setLayerType(View.LAYER_TYPE_HARDWARE, null)
                    WebView.setWebContentsDebuggingEnabled(true)

                    settings.javaScriptEnabled = true
                    settings.javaScriptCanOpenWindowsAutomatically = true
//                    settings.useWideViewPort = true
//                    settings.loadWithOverviewMode = true

//                    settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36 Edg/91.0.864.59"
//                    settings.userAgentString = "Mozilla/5.0 (Linux; U; Android 4.0.3; ko-kr; LG-L160L Build/IML74K) AppleWebkit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"


                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString()


                            // Check against the blacklist
                            if (isBlacklistedChecked && url != null && isBlockedWebsite(url)) {
                                loadUrl("this file:///$url") // Load default page
                                return true // Prevent loading the original URL
                            }

                            // Check against the whitelist
                             if (isWhitelistedChecked && url != null && isWhitelistWebsite(url)) {
                                loadUrl("file:///android_asset/error_page.html") // Load default page
                                return true // Prevent loading the original URL
                            }
                            return false // Allow the WebView to load the URL
                        }


                        private fun isBlockedWebsite(url: String): Boolean {
                            for (blockedSite in blockedWebsites) {
                                if (url.contains(blockedSite, true)) {
                                    return true
                                }
                            }

                            return false
                        }

                        private fun isWhitelistWebsite(url: String): Boolean {
                            for (allowedWebsite in allowedWebsites) {
                                if (url.contains(allowedWebsite, true)) {
                                    return false
                                }
                            }
                            return true
                        }
                    }

                    loadUrl("https://www.google.com/") // Load initial URL
                }
            },
            modifier = Modifier.fillMaxSize()
        )


    }




