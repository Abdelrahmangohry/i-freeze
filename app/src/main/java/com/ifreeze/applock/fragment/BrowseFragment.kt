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
import com.ifreeze.data.cash.PreferencesGateway


class BrowseFragment : Fragment() {
    // Variables to hold the URL and the state of blacklist/whitelist checks
    private var urlNew: String? = null
    private var isBlacklistedChecked: Boolean = false
    private var isWhitelistedChecked: Boolean = false
    lateinit var binding: FragmentBrowseBinding
    var webIcon: Bitmap? = null

    // Preference manager and lists to hold blocked/allowed websites
    lateinit var preference: PreferencesGateway
    lateinit var blockedWebsites: ArrayList<String>
    lateinit var allowedWebsites: ArrayList<String>

    // Companion object to create new instances of the fragment with arguments
    companion object {
        private const val ARG_URL = "url" // Argument key for URL

        // Function to create a new instance of BrowseFragment with the provided URL
        fun newInstance(url: String): BrowseFragment {
            val fragment = BrowseFragment()
            val args = Bundle()
            args.putString(ARG_URL, url) // Pass the URL as an argument to the fragment
            fragment.arguments = args
            return fragment
        }
    }

    // Called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retrieve the URL from the arguments if available
        arguments?.let {
            urlNew = it.getString(ARG_URL)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the fragment layout and bind it to the view
        val view = inflater.inflate(R.layout.fragment_browse, container, false)
        binding = FragmentBrowseBinding.bind(view)

        // Register the WebView for a context menu
        registerForContextMenu(binding.webView)

        // Initialize preferences and load settings for blacklist/whitelist checks
        preference = PreferencesGateway(requireContext())
        isBlacklistedChecked = preference.load("WebBlacklist", false) ?: false
        isWhitelistedChecked = preference.load("WebWhitelist", false) ?: false

        // Load the lists of blocked and allowed websites from preferences
        blockedWebsites = preference.getList("blockedWebsites")
        allowedWebsites = preference.getList("allowedWebsites")

        // Enable WebView debugging (useful for development)
        WebView.setWebContentsDebuggingEnabled(true)
        // Enable JavaScript in the WebView
        binding.webView.settings.javaScriptEnabled = true
        // Allow JavaScript to open windows automatically
        binding.webView.settings.javaScriptCanOpenWindowsAutomatically = true

        // Set a custom WebViewClient to handle URL loading and blocking logic
        binding.webView.webViewClient = MyWebViewClient(isBlacklistedChecked, isWhitelistedChecked)

        // Reload the WebView to refresh its content
        binding.webView.reload()

        // Load the initial URL after checking if it should be blocked/allowed
        binding.webView.post {
            urlNew?.let {
                // If blacklisting is enabled and the URL is blocked, load an error page
                if (isBlacklistedChecked && isBlockedWebsite(it)) {
                    binding.webView.loadUrl("file:///android_asset/error_page.html")
                    // If whitelisting is enabled and the URL is not allowed, load an error page
                } else if (isWhitelistedChecked && isWhitelistWebsite(it)) {
                    binding.webView.loadUrl("file:///android_asset/error_page.html")
                    // Otherwise, load the URL normally
                } else {
                    binding.webView.loadUrl("https://$it")
                }
            }
        }

        // Return the view to be displayed
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
        // Function to check if the URL is blocked
        private fun isBlockedWebsite(url: String): Boolean {
            for (blockedSite in blockedWebsites) {
                // Check if the URL contains any blocked site
                if (url.contains(blockedSite, true)) {
                    return true
                }
            }
            return false
        }

        // Function to check if the URL is allowed (whitelisted)
        private fun isWhitelistWebsite(url: String): Boolean {
            for (allowedWebsite in allowedWebsites) {
                // Check if the URL contains any allowed site
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

    // Method called when the fragment is paused
    override fun onPause() {
        super.onPause()
        // Save bookmarks in the parent activity
        (requireActivity() as MainWebActivity).saveBookmarks()
        // Clear all WebView data when the fragment is paused
        binding.webView.apply {
            clearMatches() // Clear text matches in WebView
            clearHistory() // Clear browsing history
            clearFormData() // Clear form data
            clearSslPreferences() // Clear SSL preferences
            clearCache(true) // Clear cache

            // Remove all cookies and web storage data
            CookieManager.getInstance().removeAllCookies(null)
            WebStorage.getInstance().deleteAllData()
        }

    }
    // Method to create a context menu when long-pressing on the WebView
    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        // Get information about what was clicked in the WebView
        val result = binding.webView.hitTestResult
        when (result.type) {
            // If an image was clicked, add options to view, save, share, or close it
            WebView.HitTestResult.IMAGE_TYPE -> {
                menu.add("View Image")
                menu.add("Save Image")
                menu.add("Share")
                menu.add("Close")
            }

            // If a link was clicked, add options to open in a new tab or share it
            WebView.HitTestResult.SRC_ANCHOR_TYPE, WebView.HitTestResult.ANCHOR_TYPE -> {
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share")
                menu.add("Close")
            }
            // Handle other types or unknown hits
            WebView.HitTestResult.EDIT_TEXT_TYPE, WebView.HitTestResult.UNKNOWN_TYPE -> {}
            else -> {
                menu.add("Open in New Tab")
                menu.add("Open Tab in Background")
                menu.add("Share")
                menu.add("Close")
            }
        }
    }

    // Method called when a context menu item is selected
    override fun onContextItemSelected(item: MenuItem): Boolean {

        // Get the URL or image URL of the item that was long-pressed
        val message = Handler().obtainMessage()
        binding.webView.requestFocusNodeHref(message)
        val url = message.data.getString("url")
        val imgUrl = message.data.getString("src")

        // Handle different context menu options based on the selected item
        when (item.title) {
            // Open the selected URL in a new tab
            "Open in New Tab" -> {
                changeTab(url.toString(), BrowseFragment.newInstance(url.toString()))
            }
            // Open the selected URL in a new tab in the background
            "Open Tab in Background" -> {
                changeTab(
                    url.toString(),
                    BrowseFragment.newInstance(url.toString()),
                    isBackground = true
                )

            }
            // View the selected image in a dialog
            "View Image" -> {
                imgUrl?.let {
                    // Handle base64 encoded images
                    if (it.contains("base64")) {
                        val pureBytes = it.substring(it.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        val imgView = ShapeableImageView(requireContext())
                        imgView.setImageBitmap(finalImg)
                        val imgDialog =
                            MaterialAlertDialogBuilder(requireContext()).setView(imgView).create()
                        imgDialog.show()
                        // Set image dialog dimensions
                        imgView.layoutParams.width =
                            Resources.getSystem().displayMetrics.widthPixels
                        imgView.layoutParams.height =
                            (Resources.getSystem().displayMetrics.heightPixels * .75).toInt()
                        imgView.requestLayout()
                        imgDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    } else {
                        changeTab(it, BrowseFragment.newInstance(it))
                    }
                }
            }
            // Save the selected image to the device
            "Save Image" -> {
                if (imgUrl != null) {
                    // Handle base64 encoded images
                    if (imgUrl.contains("base64")) {
                        val pureBytes = imgUrl.substring(imgUrl.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        // Save image to device's gallery
                        MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            finalImg, "Image", null
                        )
                        Snackbar.make(binding.root, "Image Saved Successfully", 3000).show()
                        // Open the image URL in a browser
                    } else startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse(imgUrl)))
                }
            }
            // Share the selected URL or image
            "Share" -> {
                val tempUrl = url ?: imgUrl
                if (tempUrl != null) {
                    if (tempUrl.contains("base64")) {

                        val pureBytes = tempUrl.substring(tempUrl.indexOf(",") + 1)
                        val decodedBytes = Base64.decode(pureBytes, Base64.DEFAULT)
                        val finalImg =
                            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                        // Share the image
                        val path = MediaStore.Images.Media.insertImage(
                            requireActivity().contentResolver,
                            finalImg, "Image", null
                        )
                        // Share the text URL
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
            // Close the context menu without any action
            "Close" -> {}
        }

        return super.onContextItemSelected(item)
    }

}