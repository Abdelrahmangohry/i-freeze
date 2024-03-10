package com.lock.applock.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.util.Base64
import android.view.*
import android.webkit.*
import android.widget.Toast
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.lock.applock.R
import com.lock.applock.databinding.FragmentBrowseBinding
import com.lock.applock.presentation.activity.MainWebActivity
import com.lock.applock.presentation.activity.changeTab
import com.patient.data.cashe.PreferencesGateway
import java.io.ByteArrayOutputStream


class BrowseFragment(private var urlNew: String) : Fragment() {

    lateinit var binding: FragmentBrowseBinding
    var webIcon: Bitmap? = null

    lateinit var preference: PreferencesGateway
    lateinit var blockedWebsites: ArrayList<String>
    lateinit var allowedWebsites: ArrayList<String>

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        binding = FragmentBrowseBinding.bind(view)
        registerForContextMenu(binding.webView)
        preference = PreferencesGateway(requireContext())
        val isBlacklistedChecked: Boolean? = preference.load("WebBlacklist", false)
        val isWhitelistedChecked = preference.load("WebWhitelist", false)


        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")
        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true


        binding.webView.webViewClient = MyWebViewClient(isBlacklistedChecked, isWhitelistedChecked)

        // binding.webView.loadUrl(urlNew)
        //  binding.webView.loadUrl("https://www.google.com/search?q=$urlNew")
        binding.webView.post {
            if (isBlacklistedChecked == true && isBlockedWebsite(urlNew)) {
                binding.webView.loadUrl("file:///android_asset/error_page.html")
            } else if (isWhitelistedChecked == true && isWhitelistWebsite(urlNew)) {
                binding.webView.loadUrl("file:///android_asset/error_page.html")

            } else {
                binding.webView.loadUrl("https://www.google.com/search?q=$urlNew")

            }

        }



        return view

    }

    private inner class MyWebViewClient(
        val isBlacklistedChecked: Boolean?,
        val isWhitelistedChecked: Boolean?
    ) : WebViewClient() {

        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val url = request?.url.toString()

            // Check against the blacklist
            if (isBlacklistedChecked == true && isBlockedWebsite(url)) {
                view?.loadUrl("this file:///$url") // Load default page
                return true // Prevent loading the original URL
            }

            // Check against the whitelist
            if (isWhitelistedChecked == true && isWhitelistWebsite(url)) {
                view?.loadUrl("file:///android_asset/error_page.html") // Load default page
                return true // Prevent loading the original URL
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

        private fun isWhitelistWebsite(url: String): Boolean {
            for (allowedWebsite in allowedWebsites) {
                if (url.contains(allowedWebsite, true)) {
                    return false
                }
            }
            return true
        }

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

    @SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")
        MainWebActivity.tabsList[MainWebActivity.myPager.currentItem].name =
            binding.webView.url.toString()
        MainWebActivity.tabsBtn.text = MainWebActivity.tabsList.size.toString()

        //for downloading file using external download manager
        binding.webView.setDownloadListener { url, _, _, _, _ ->
            startActivity(
                Intent(Intent.ACTION_VIEW).setData(
                    Uri.parse(url)
                )
            )
        }

        val mainRef = requireActivity() as MainWebActivity

        mainRef.binding.refreshBtn.visibility = View.VISIBLE
        mainRef.binding.refreshBtn.setOnClickListener {
            binding.webView.reload()
        }

        binding.webView.apply {
            settings.javaScriptEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            webViewClient = object : WebViewClient() {

                override fun onLoadResource(view: WebView?, url: String?) {
                    super.onLoadResource(view, url)
                    if (MainWebActivity.isDesktopSite)
                        view?.evaluateJavascript(
                            "document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                    " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));",
                            null
                        )
                }

                override fun doUpdateVisitedHistory(
                    view: WebView?,
                    url: String?,
                    isReload: Boolean
                ) {
                    super.doUpdateVisitedHistory(view, url, isReload)
                    mainRef.binding.topSearchBar.text = SpannableStringBuilder(url)
                    MainWebActivity.tabsList[MainWebActivity.myPager.currentItem].name =
                        url.toString()
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    mainRef.binding.progressBar.progress = 0
                    mainRef.binding.progressBar.visibility = View.VISIBLE
                    if (url!!.contains(
                            "you",
                            ignoreCase = false
                        )
                    ) mainRef.binding.root.transitionToEnd()
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    mainRef.binding.progressBar.visibility = View.GONE
                    binding.webView.zoomOut()
                }
            }
            webChromeClient = object : WebChromeClient() {
                //for setting icon to our search bar
                override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                    super.onReceivedIcon(view, icon)
                    try {
                        mainRef.binding.webIcon.setImageBitmap(icon)
                        webIcon = icon
                        MainWebActivity.bookmarkIndex = mainRef.isBookmarked(view?.url!!)
                        if (MainWebActivity.bookmarkIndex != -1) {
                            val array = ByteArrayOutputStream()
                            icon!!.compress(Bitmap.CompressFormat.PNG, 100, array)
                            MainWebActivity.bookmarkList[MainWebActivity.bookmarkIndex].image =
                                array.toByteArray()
                        }
                    } catch (e: Exception) {
                    }
                }

                override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                    super.onShowCustomView(view, callback)
                    binding.webView.visibility = View.GONE
                    binding.customView.visibility = View.VISIBLE
                    binding.customView.addView(view)
                    mainRef.binding.root.transitionToEnd()
                }

                override fun onHideCustomView() {
                    super.onHideCustomView()
                    binding.webView.visibility = View.VISIBLE
                    binding.customView.visibility = View.GONE

                }

                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    mainRef.binding.progressBar.progress = newProgress
                }
            }

            binding.webView.setOnTouchListener { _, motionEvent ->
                mainRef.binding.root.onTouchEvent(motionEvent)
                return@setOnTouchListener false
            }

            binding.webView.reload()
        }


    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainWebActivity).saveBookmarks()
        //for clearing all webview data
        binding.webView.apply {
            clearMatches()
            clearHistory()
            clearFormData()
            clearSslPreferences()
            clearCache(true)

            CookieManager.getInstance().removeAllCookies(null)
            WebStorage.getInstance().deleteAllData()
        }

    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")
        val result = binding.webView.hitTestResult
        when (result.type) {
            WebView.HitTestResult.IMAGE_TYPE -> {
                menu.add("View Image")
                menu.add("Save Image")
                menu.add("Share")
                menu.add("Close")
            }

            WebView.HitTestResult.SRC_ANCHOR_TYPE, WebView.HitTestResult.ANCHOR_TYPE -> {
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share")
                menu.add("Close")
            }

            WebView.HitTestResult.EDIT_TEXT_TYPE, WebView.HitTestResult.UNKNOWN_TYPE -> {}
            else -> {
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share")
                menu.add("Close")
            }
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")
        val message = Handler().obtainMessage()
        binding.webView.requestFocusNodeHref(message)
        val url = message.data.getString("url")
        val imgUrl = message.data.getString("src")

        when (item.title) {
            "Open in New Tab" -> {
                changeTab(url.toString(), BrowseFragment(url.toString()))
            }

            "Open Tab in Background" -> {
                changeTab(url.toString(), BrowseFragment(url.toString()), isBackground = true)
            }

            "View Image" -> {
                if (imgUrl != null) {
                    if (imgUrl.contains("base64")) {
                        val pureBytes = imgUrl.substring(imgUrl.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        val imgView = ShapeableImageView(requireContext())
                        imgView.setImageBitmap(finalImg)

                        val imgDialog =
                            MaterialAlertDialogBuilder(requireContext()).setView(imgView).create()
                        imgDialog.show()

                        imgView.layoutParams.width =
                            Resources.getSystem().displayMetrics.widthPixels
                        imgView.layoutParams.height =
                            (Resources.getSystem().displayMetrics.heightPixels * .75).toInt()
                        imgView.requestLayout()

                        imgDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    } else changeTab(imgUrl, BrowseFragment(imgUrl))
                }
            }

            "Save Image" -> {
                if (imgUrl != null) {
                    if (imgUrl.contains("base64")) {
                        val pureBytes = imgUrl.substring(imgUrl.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            finalImg, "Image", null
                        )
                        Snackbar.make(binding.root, "Image Saved Successfully", 3000).show()
                    } else startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(imgUrl)))
                }
            }

            "Share" -> {
                val tempUrl = url ?: imgUrl
                if (tempUrl != null) {
                    if (tempUrl.contains("base64")) {

                        val pureBytes = tempUrl.substring(tempUrl.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

                        val path = MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            finalImg, "Image", null
                        )

                        ShareCompat.IntentBuilder(requireContext()).setChooserTitle("Sharing Url!")
                            .setType("image/*")
                            .setStream(Uri.parse(path))
                            .startChooser()
                    } else {
                        ShareCompat.IntentBuilder(requireContext()).setChooserTitle("Sharing Url!")
                            .setType("text/plain").setText(tempUrl)
                            .startChooser()
                    }
                } else Snackbar.make(binding.root, "Not a Valid Link!", 3000).show()
            }

            "Close" -> {}
        }

        return super.onContextItemSelected(item)
    }

}