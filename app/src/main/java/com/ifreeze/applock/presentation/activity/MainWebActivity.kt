package com.ifreeze.applock.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.view.Gravity
import android.webkit.WebView
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.ifreeze.applock.presentation.adapter.TabAdapter
import com.ifreeze.data.model.Bookmark
import com.ifreeze.data.model.Tab
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textview.MaterialTextView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.ifreeze.applock.MainActivity
import com.ifreeze.applock.R
import com.ifreeze.applock.databinding.ActivityMainWebBinding
import com.ifreeze.applock.databinding.BookmarkDialogBinding
import com.ifreeze.applock.databinding.MoreFeaturesBinding
import com.ifreeze.applock.databinding.TabsViewBinding
import com.ifreeze.applock.fragment.BrowseFragment
import com.ifreeze.applock.fragment.HomeFragment
import com.ifreeze.applock.presentation.activity.MainWebActivity.Companion.myPager
import com.ifreeze.applock.presentation.activity.MainWebActivity.Companion.tabsBtn
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainWebActivity : AppCompatActivity() {
    // View binding instance to access UI components
    lateinit var binding: ActivityMainWebBinding

    // Current print job for saving WebView content as PDF
    private var printJob: PrintJob? = null

    companion object {
        // List of open tabs in the WebView
        var tabsList: ArrayList<Tab> = ArrayList()

        // Flag to indicate if fullscreen mode is enabled
        private var isFullscreen: Boolean = true

        // Flag to indicate if desktop site mode is enabled
        var isDesktopSite: Boolean = false

        // List of saved bookmarks
        var bookmarkList: ArrayList<Bookmark> = ArrayList()

        // Index of the current bookmark if it exists
        var bookmarkIndex: Int = -1

        // ViewPager2 for tab management
        lateinit var myPager: ViewPager2

        // Button to open tab management dialog
        lateinit var tabsBtn: MaterialTextView

        // Button for navigating back in the WebView
        lateinit var backArrow: ImageView

        // FloatingActionButton to launch MainActivity
        lateinit var mainActivity: FloatingActionButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout and bind UI components
        binding = ActivityMainWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize UI components
        myPager = binding.myPager
        tabsBtn = binding.tabsBtn
        backArrow = binding.backArrow
        mainActivity = binding.mainActivity

        // Handle incoming intent
        handleIntent(intent)
        // Load bookmarks from shared preferences
        getAllBookmarks()

        // Initialize default tab
        tabsList.add(Tab("Home", HomeFragment()))
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        binding.myPager.isUserInputEnabled = false

        // Set up UI and fullscreen mode
        initializeView()
        changeFullscreen(enable = true)
        // Set up click listeners for back arrow and floating action button
        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.mainActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Handle the new intent with a URL
        handleIntent(intent)
    }

    /**
     * Processes the intent to load a URL if provided.
     * @param intent The incoming intent
     */
    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            // A URL was provided in the intent, load it in the WebView or handle it as needed
            loadUrl(uri.toString())
        }
    }

    /**
     * Loads a URL into the WebView by creating a new tab.
     * @param url The URL to load
     */
    private fun loadUrl(url: String) {
        // Remove "https://" or "http://" from the URL
        val newUrl = url.replace("https://", "")
        changeTab(url, BrowseFragment.newInstance(newUrl))
        changeFullscreen(enable = true)
    }


    @SuppressLint("NotifyDataSetChanged")
    override fun onBackPressed() {
        var frag: BrowseFragment? = null
        try {
            frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
        } catch (e: Exception) {
            // Handle exceptions related to fragment casting
        }

        when {
            frag?.binding?.webView?.canGoBack() == true ->
                // Navigate back in WebView
                frag?.binding!!.webView.goBack()

            binding.myPager.currentItem != 0 -> {
                // Close current tab and navigate to the previous one
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1

            }
            // Perform default back navigation
            else -> super.onBackPressed()
        }
    }

    /**
     * Adapter class for managing tab fragments in ViewPager2.
     */
    private inner class TabsAdapter(fa: FragmentManager, lc: Lifecycle) :
        FragmentStateAdapter(fa, lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position].fragment
    }

    /**
     * Initializes view components and sets up dialogs.
     */
    private fun initializeView() {
        // Handle tabs management button click
        binding.tabsBtn.setOnClickListener {
            val viewTabs = layoutInflater.inflate(R.layout.tabs_view, binding.root, false)
            val bindingTabs = TabsViewBinding.bind(viewTabs)

            val dialogTabs =
                MaterialAlertDialogBuilder(this, R.style.Theme_AppLock).setView(viewTabs)
                    .setTitle("Select Tab")
                    .setNeutralButton("New Tab") { self, _ ->
                        // Create a new tab
                        changeTab("Home", HomeFragment())
                        self.dismiss()
                    }
                    .create()
            // Set up tabs RecyclerView in the dialog
            bindingTabs.tabsRV.setHasFixedSize(true)
            bindingTabs.tabsRV.layoutManager = LinearLayoutManager(this)
            bindingTabs.tabsRV.adapter = TabAdapter(this, dialogTabs)

            dialogTabs.show()
            // Customize dialog buttons
            val pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE)
            val nBtn = dialogTabs.getButton(AlertDialog.BUTTON_NEUTRAL)

            pBtn.isAllCaps = false
            nBtn.isAllCaps = false

            pBtn.setTextColor(Color.BLACK)
            nBtn.setTextColor(Color.BLACK)

            pBtn.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_home, theme), null, null, null
            )
            nBtn.setCompoundDrawablesWithIntrinsicBounds(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_add, theme), null, null, null
            )
        }
        // Handle settings button click
        binding.settingBtn.setOnClickListener {

            var frag: BrowseFragment? = null
            try {
                frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
            } catch (e: Exception) {
                // Handle exceptions related to fragment casting
            }

            val view = layoutInflater.inflate(R.layout.more_features, binding.root, false)
            val dialogBinding = MoreFeaturesBinding.bind(view)

            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()
            // Customize dialog window attributes
            dialog.window?.apply {
                attributes.gravity = Gravity.BOTTOM
                attributes.y = 50
                setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }
            dialog.show()
            // Set up fullscreen button in dialog
            if (isFullscreen) {
                dialogBinding.fullscreenBtn.apply {
                    setIconTintResource(R.color.blue)
                    setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                }
            }
            // Set up bookmark button in dialog
            frag?.let {
                bookmarkIndex = isBookmarked(it.binding.webView.url!!)
                if (bookmarkIndex != -1) {

                    dialogBinding.bookmarkBtn.apply {
                        setIconTintResource(R.color.blue)
                        setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                    }
                }
            }
            // Set up desktop site button in dialog
            if (isDesktopSite) {
                dialogBinding.desktopBtn.apply {
                    setIconTintResource(R.color.blue)
                    setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                }
            }


            // Handle dialog button clicks
            dialogBinding.backBtn.setOnClickListener {
                onBackPressed()
            }

            dialogBinding.forwardBtn.setOnClickListener {
                frag?.apply {
                    if (binding.webView.canGoForward())
                        binding.webView.goForward()
                }
            }

            dialogBinding.saveBtn.setOnClickListener {
                dialog.dismiss()
                if (frag != null)
                    saveAsPdf(web = frag.binding.webView)
                else Snackbar.make(binding.root, "First Open A WebPage\uD83D\uDE03", 3000).show()
            }

            dialogBinding.fullscreenBtn.setOnClickListener {
                it as MaterialButton

                isFullscreen = if (isFullscreen) {
                    changeFullscreen(enable = false)
                    it.setIconTintResource(R.color.black)
                    it.setTextColor(ContextCompat.getColor(this, R.color.black))
                    false
                } else {
                    changeFullscreen(enable = true)
                    it.setIconTintResource(R.color.blue)
                    it.setTextColor(ContextCompat.getColor(this, R.color.blue))
                    true
                }
            }

            dialogBinding.desktopBtn.setOnClickListener {
                it as MaterialButton

                frag?.binding?.webView?.apply {
                    isDesktopSite = if (isDesktopSite) {
                        settings.userAgentString = null
                        it.setIconTintResource(R.color.black)
                        it.setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.black))
                        false
                    } else {
                        settings.userAgentString =
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0"
                        settings.useWideViewPort = true
                        evaluateJavascript(
                            "document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                    " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));",
                            null
                        )
                        it.setIconTintResource(R.color.blue)
                        it.setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                        true
                    }
                    reload()
                    dialog.dismiss()
                }

            }

            dialogBinding.bookmarkBtn.setOnClickListener {
                frag?.let {
                    if (bookmarkIndex == -1) {
                        val viewB =
                            layoutInflater.inflate(R.layout.bookmark_dialog, binding.root, false)
                        val bBinding = BookmarkDialogBinding.bind(viewB)
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Add Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
                            .setPositiveButton("Add") { self, _ ->
                                try {
                                    val array = ByteArrayOutputStream()
                                    it.webIcon?.compress(Bitmap.CompressFormat.PNG, 100, array)
                                    bookmarkList.add(
                                        Bookmark(
                                            name = bBinding.bookmarkTitle.text.toString(),
                                            url = it.binding.webView.url!!,
                                            array.toByteArray()
                                        )
                                    )
                                } catch (e: Exception) {
                                    bookmarkList.add(
                                        Bookmark(
                                            name = bBinding.bookmarkTitle.text.toString(),
                                            url = it.binding.webView.url!!
                                        )
                                    )
                                }
                                self.dismiss()
                            }
                            .setNegativeButton("Cancel") { self, _ -> self.dismiss() }
                            .setView(viewB).create()
                        dialogB.show()
                        bBinding.bookmarkTitle.setText(it.binding.webView.title)
                    } else {
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Remove Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
                            .setPositiveButton("Remove") { self, _ ->
                                bookmarkList.removeAt(bookmarkIndex)
                                self.dismiss()
                            }
                            .setNegativeButton("Cancel") { self, _ -> self.dismiss() }
                            .create()
                        dialogB.show()
                    }
                }

                dialog.dismiss()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        printJob?.let {
            when {
                it.isCompleted -> Snackbar.make(
                    binding.root,
                    "Successful -> ${it.info.label}",
                    4000
                ).show()

                it.isFailed -> Snackbar.make(binding.root, "Failed -> ${it.info.label}", 4000)
                    .show()
            }
        }
    }

    /**
     * Saves the current WebView content as a PDF.
     * @param web The WebView to save as PDF
     */
    private fun saveAsPdf(web: WebView) {
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${URL(web.url).host}_${
            SimpleDateFormat("HH:mm d_MMM_yy", Locale.ENGLISH)
                .format(Calendar.getInstance().time)
        }"
        val printAdapter = web.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
        printJob = pm.print(jobName, printAdapter, printAttributes.build())
    }

    /**
    * Toggles fullscreen mode in the activity.
    * @param enable Whether to enable or disable fullscreen mode
    */
    private fun changeFullscreen(enable: Boolean) {
        if (enable) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
//                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(
                window,
                binding.root
            ).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun isBookmarked(url: String): Int {
        bookmarkList.forEachIndexed { index, bookmark ->
            if (bookmark.url == url) return index
        }
        return -1
    }
    /**
     * Saves the current list of bookmarks to shared preferences.
     */
    fun saveBookmarks() {
        //for storing bookmarks data using shared preferences
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE).edit()

        val data = GsonBuilder().create().toJson(bookmarkList)
        editor.putString("bookmarkList", data)

        editor.apply()
    }
    /**
     * Loads all bookmarks from shared preferences.
     */
    private fun getAllBookmarks() {
        //for getting bookmarks data using shared preferences from storage
        bookmarkList = ArrayList()
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE)
        val data = editor.getString("bookmarkList", null)

        if (data != null) {
            val list: ArrayList<Bookmark> = GsonBuilder().create()
                .fromJson(data, object : TypeToken<ArrayList<Bookmark>>() {}.type)
            bookmarkList.addAll(list)
        }
    }

}

/**
 * Adds or updates a tab in the ViewPager2.
 * @param title The title of the tab
 * @param fragment The fragment to display in the tab
 */
@SuppressLint("NotifyDataSetChanged")
fun changeTab(url: String, fragment: Fragment, isBackground: Boolean = false) {
    MainWebActivity.tabsList.add(Tab(name = url, fragment = fragment))
    myPager.adapter?.notifyDataSetChanged()
    tabsBtn.text = MainWebActivity.tabsList.size.toString()

    if (!isBackground) myPager.currentItem = MainWebActivity.tabsList.size - 1
}

fun checkForInternet(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    } else {
        @Suppress("DEPRECATION") val networkInfo =
            connectivityManager.activeNetworkInfo ?: return false
        @Suppress("DEPRECATION")
        return networkInfo.isConnected
    }
}