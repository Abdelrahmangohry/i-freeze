package com.lock.applock.presentation.activity

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.patient.data.cashe.PreferencesGateway
import java.net.URL





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
                    allowedWebsites = allowedWebsites,
                    navController
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
    allowedWebsites: List<String>,
    navController: NavController

) {



   TabManager(navController)



}

@Composable
fun TabContent(url: String,navController: NavController) {
    val preference = PreferencesGateway(LocalContext.current)
    val isBlacklistedChecked: MutableState<Boolean?> = remember { mutableStateOf(preference.load("WebBlacklist", false)) }
    val isWhitelistedChecked: MutableState<Boolean?> = remember { mutableStateOf(preference.load("WebWhitelist", false)) }
    var blockedWebsites = preference.getList("blockedWebsites")
    var allowedWebsites = preference.getList("allowedWebsites")
    var webView: WebView? = null
    val backEnabled by remember { mutableStateOf(true) }
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
                        if (isBlacklistedChecked.value == true && url != null && isBlockedWebsite(url)) {
                            loadUrl("this file:///$url") // Load default page
                            return true // Prevent loading the original URL
                        }

                        // Check against the whitelist
                        if (isWhitelistedChecked.value == true && url != null && isWhitelistWebsite(url)) {
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
                webView = this

            }
        },
        modifier = Modifier.fillMaxSize(),
        update = {
            webView = it
        }
    )
    BackHandler(enabled = backEnabled) {
        if (webView?.canGoBack() == true)
            webView?.goBack()
        else
            navController.popBackStack()

    }
}

@Composable
fun TabManager(navController: NavController) {
    val u = "www.facebook.com/lol"
    val url = URL(u)
    val host = url.host
    var tabs by remember { mutableStateOf(listOf("https://example.com")) }
    var currentTab by remember { mutableStateOf(0) }

    Column {
        // Tab bar UI
        TabRow(selectedTabIndex = currentTab) {
            tabs.forEachIndexed { index, tabUrl ->
                Tab(
                    selected = currentTab == index,
                    onClick = { currentTab = index }
                ) {
                    Text(tabUrl)
                }
            }
            // Add new tab button
            IconButton(onClick = { tabs = tabs + "https://example.com" }) {
                Icon(Icons.Default.Add, contentDescription = "Add Tab")
            }
        }

        // Tab content
        TabContent(url = tabs[currentTab], navController)
    }
}



