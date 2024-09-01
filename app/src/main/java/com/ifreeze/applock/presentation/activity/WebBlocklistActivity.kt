package com.ifreeze.applock.presentation.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import com.ifreeze.data.cash.PreferencesGateway

class WebBlocklistActivity : AppCompatActivity() {
    private lateinit var myWebView: WebView
    private lateinit var blockedWebsites: ArrayList<String>
    private lateinit var sharedPrefManager: PreferencesGateway
    private lateinit var urlEditText: EditText
    private lateinit var imgGo: ImageButton


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_web_blocklist)

        initView()
        setUpData()

        myWebView.settings.javaScriptEnabled = true
        myWebView.webViewClient = MyWebViewClient()

        if (blockedWebsites.size == 0)
            loadDefaultContent(myWebView)
        else
            myWebView.loadUrl("https://www.google.com/")

        imgGo.setOnClickListener {
            loadUrlOrSearch()

        }

        urlEditText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                loadUrlOrSearch()
                return@setOnKeyListener true
            }
            false
        }


    }

    private fun loadUrlOrSearch() {
        val inputText = urlEditText.text.toString()
        if (inputText.isNotEmpty()) {
            if (inputText.startsWith("http://") || inputText.startsWith("https://")) {
                // Load the URL directly
                myWebView.loadUrl(inputText)
            } else {
                // Perform a Google search
                myWebView.loadUrl("https://www.google.com/search?q=$inputText")
            }
        }
    }

    private fun loadDefaultContent(webView: WebView) {
        val htmlContent =
            "<html><body><h1>Welcome to My App</h1><p>This is the default content.</p></body></html>"

        // Load HTML content into WebView
        webView.loadData(htmlContent, "text/html", "UTF-8")
    }

    private fun setUpData() {
        blockedWebsites = ArrayList()

        sharedPrefManager = PreferencesGateway(this)

        blockedWebsites = sharedPrefManager.getList("blockedWebsites")

    }


    private fun initView() {
//        myWebView = findViewById(R.id.myWebView)
//        urlEditText = findViewById(R.id.urlEditText)
//        imgGo = findViewById(R.id.imgGo)


    }

    private inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url?.toString()

            // Check if the URL is in the list of blocked websites
            if (url != null && isBlockedWebsite(url)) {
                // Handle the blocked website, for example, show a message or load a different URL
                // For demonstration purposes, let's load a default error page
                view?.loadUrl("file:///android_asset/error_page.html")
                return true  // URL loading handled by WebView, so return true
            }

            // Allow loading of other URLs
            return false
        }

        private fun isBlockedWebsite(url: String): Boolean {
            for (blockedSite in blockedWebsites) {
                if (url.contains(blockedSite, true)) {
                    return true
                }
            }
            return false
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (myWebView.canGoBack())
            myWebView.goBack()
    }
}