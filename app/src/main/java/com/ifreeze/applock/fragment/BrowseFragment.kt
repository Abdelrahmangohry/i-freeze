package com.ifreeze.applock.fragment

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
import android.util.Base64
import android.util.Log
import android.view.*
import android.webkit.*
import androidx.core.app.ShareCompat
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.FragmentBrowseBinding
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.changeTab
import com.patient.data.cashe.PreferencesGateway


class BrowseFragment(private var urlNew: String) : Fragment() {
    private var isBlacklistedChecked: Boolean = false
    private var isWhitelistedChecked: Boolean = false
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
        isBlacklistedChecked = preference.load("WebBlacklist", false) ?: false
        isWhitelistedChecked = preference.load("WebWhitelist", false) ?: false


        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")

        WebView.setWebContentsDebuggingEnabled(true)
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true


        binding.webView.webViewClient = MyWebViewClient(isBlacklistedChecked, isWhitelistedChecked)


        binding.webView.reload()

        binding.webView.post {
            if (isBlacklistedChecked && isBlockedWebsite(urlNew)) {
                binding.webView.loadUrl("file:///android_asset/error_page.html")
            } else if (isWhitelistedChecked && isWhitelistWebsite(urlNew)) {
                binding.webView.loadUrl("file:///android_asset/error_page.html")

            } else {
                binding.webView.loadUrl("https://$urlNew")
//                binding.webView.loadUrl("https://www.google.com/search?q=$urlNew")

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
            Log.d("abdo", "this is the url $url")
            // Check if the URL is an external link
            if (!url.startsWith("https://$urlNew") && !url.startsWith("http://$urlNew")) {
                // Open the external link in your WebView

                view?.loadUrl(url)
                return true
            }

            // Check against the blacklist
            if (isBlacklistedChecked == true && isBlockedWebsite(url)) {
                view?.loadUrl("this file://$url") // Load default page
                return true // Prevent loading the original URL
            }

            // Check against the whitelist
            if (isWhitelistedChecked == true && isWhitelistWebsite(url)) {
                view?.loadUrl("file://android_asset/error_page.html") // Load default page
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