package com.ifreeze.applock.presentation.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.VectorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.print.PrintAttributes
import android.print.PrintJob
import android.print.PrintManager
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.webkit.WebView
import android.widget.Button
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
import com.example.browser.adapter.TabAdapter
import com.ifreeze.applock.fragment.BrowseFragment
import com.ifreeze.applock.fragment.HomeFragment
import com.example.browser.model.Bookmark
import com.example.browser.model.Tab
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
import com.ifreeze.applock.presentation.activity.MainWebActivity.Companion.myPager
import com.ifreeze.applock.presentation.activity.MainWebActivity.Companion.tabsBtn
import java.io.ByteArrayOutputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale

class MainWebActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainWebBinding
    private var printJob: PrintJob? = null

    companion object{
        var tabsList: ArrayList<Tab> = ArrayList()
        private var isFullscreen: Boolean = true
        var isDesktopSite: Boolean = false
        var bookmarkList: ArrayList<Bookmark> = ArrayList()
        var bookmarkIndex: Int = -1
        lateinit var myPager: ViewPager2
        lateinit var tabsBtn: MaterialTextView
        lateinit var backArrow : ImageView
        lateinit var mainActivity : FloatingActionButton
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainWebBinding.inflate(layoutInflater)
        setContentView(binding.root)

// Initialize myPager here
        myPager = binding.myPager
        tabsBtn = binding.tabsBtn
        backArrow  = binding.backArrow
        mainActivity  = binding.mainActivity


        handleIntent(intent)
        getAllBookmarks()

        tabsList.add(Tab("Home", HomeFragment()))
        binding.myPager.adapter = TabsAdapter(supportFragmentManager, lifecycle)
        binding.myPager.isUserInputEnabled = false


        initializeView()
        changeFullscreen(enable = true)

        binding.backArrow.setOnClickListener {
            onBackPressed()
        }

        binding.mainActivity.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        // Handle the new intent with a URL
//        handleIntent(intent)
//    }
    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            // A URL was provided in the intent, load it in the WebView or handle it as needed
            loadUrl(uri.toString())
        }
    }

    private fun loadUrl(url: String) {
        Log.d("abdo", "this is the url should opened $url")
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
        }catch (e:Exception){}

        when{
            frag?.binding?.webView?.canGoBack() == true -> frag?.binding!!.webView.goBack()
            binding.myPager.currentItem != 0 ->{
                tabsList.removeAt(binding.myPager.currentItem)
                binding.myPager.adapter?.notifyDataSetChanged()
                binding.myPager.currentItem = tabsList.size - 1

            }
            else -> super.onBackPressed()
        }
    }


    private inner class TabsAdapter(fa: FragmentManager, lc: Lifecycle) : FragmentStateAdapter(fa, lc) {
        override fun getItemCount(): Int = tabsList.size

        override fun createFragment(position: Int): Fragment = tabsList[position].fragment
    }



    private fun initializeView(){

        binding.tabsBtn.setOnClickListener {
            val viewTabs = layoutInflater.inflate(R.layout.tabs_view, binding.root, false)
            val bindingTabs = TabsViewBinding.bind(viewTabs)

            val dialogTabs = MaterialAlertDialogBuilder(this, R.style.Theme_AppLock).setView(viewTabs)
                .setTitle("Select Tab")
                .setNeutralButton("New Tab"){self, _ ->
                    changeTab("Home", HomeFragment())
                    self.dismiss()
                }
                .create()

            bindingTabs.tabsRV.setHasFixedSize(true)
            bindingTabs.tabsRV.layoutManager = LinearLayoutManager(this)
            bindingTabs.tabsRV.adapter = TabAdapter(this, dialogTabs)

            dialogTabs.show()

            val pBtn = dialogTabs.getButton(AlertDialog.BUTTON_POSITIVE)
            val nBtn = dialogTabs.getButton(AlertDialog.BUTTON_NEUTRAL)

            pBtn.isAllCaps = false
            nBtn.isAllCaps = false

            pBtn.setTextColor(Color.BLACK)
            nBtn.setTextColor(Color.BLACK)

            pBtn.setCompoundDrawablesWithIntrinsicBounds( ResourcesCompat.getDrawable(resources, R.drawable.ic_home, theme)
                , null, null, null)
            nBtn.setCompoundDrawablesWithIntrinsicBounds( ResourcesCompat.getDrawable(resources, R.drawable.ic_add, theme)
                , null, null, null)
        }

        binding.settingBtn.setOnClickListener {

            var frag: BrowseFragment? = null
            try {
                frag = tabsList[binding.myPager.currentItem].fragment as BrowseFragment
            }catch (e:Exception){}

            val view = layoutInflater.inflate(R.layout.more_features, binding.root, false)
            val dialogBinding = MoreFeaturesBinding.bind(view)

            val dialog = MaterialAlertDialogBuilder(this).setView(view).create()

            dialog.window?.apply {
                attributes.gravity = Gravity.BOTTOM
                attributes.y = 50
                setBackgroundDrawable(ColorDrawable(0xFFFFFFFF.toInt()))
            }
            dialog.show()

            if(isFullscreen){
                dialogBinding.fullscreenBtn.apply {
                    setIconTintResource(R.color.blue)
                    setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                }
            }

            frag?.let {
                bookmarkIndex = isBookmarked(it.binding.webView.url!!)
                if(bookmarkIndex != -1){

                    dialogBinding.bookmarkBtn.apply {
                        setIconTintResource(R.color.blue)
                        setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                    }
                } }

            if(isDesktopSite){
                dialogBinding.desktopBtn.apply {
                    setIconTintResource(R.color.blue)
                    setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                }
            }



            dialogBinding.backBtn.setOnClickListener {
                onBackPressed()
            }

            dialogBinding.forwardBtn.setOnClickListener {
                frag?.apply {
                    if(binding.webView.canGoForward())
                        binding.webView.goForward()
                }
            }

            dialogBinding.saveBtn.setOnClickListener {
                dialog.dismiss()
                if(frag != null)
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
                }
                else {
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
                    }
                    else {
                        settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:99.0) Gecko/20100101 Firefox/99.0"
                        settings.useWideViewPort = true
                        evaluateJavascript("document.querySelector('meta[name=\"viewport\"]').setAttribute('content'," +
                                " 'width=1024px, initial-scale=' + (document.documentElement.clientWidth / 1024));", null)
                        it.setIconTintResource(R.color.blue)
                        it.setTextColor(ContextCompat.getColor(this@MainWebActivity, R.color.blue))
                        true
                    }
                    reload()
                    dialog.dismiss()
                }

            }

            dialogBinding.bookmarkBtn.setOnClickListener {
                frag?.let{
                    if(bookmarkIndex == -1){
                        val viewB = layoutInflater.inflate(R.layout.bookmark_dialog, binding.root, false)
                        val bBinding = BookmarkDialogBinding.bind(viewB)
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Add Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
                            .setPositiveButton("Add"){self, _ ->
                                try{
                                    val array = ByteArrayOutputStream()
                                    it.webIcon?.compress(Bitmap.CompressFormat.PNG, 100, array)
                                    bookmarkList.add(
                                        Bookmark(name = bBinding.bookmarkTitle.text.toString(), url = it.binding.webView.url!!, array.toByteArray())
                                    )
                                }
                                catch(e: Exception){
                                    bookmarkList.add(
                                        Bookmark(name = bBinding.bookmarkTitle.text.toString(), url = it.binding.webView.url!!)
                                    )
                                }
                                self.dismiss()}
                            .setNegativeButton("Cancel"){self, _ -> self.dismiss()}
                            .setView(viewB).create()
                        dialogB.show()
                        bBinding.bookmarkTitle.setText(it.binding.webView.title)
                    }else{
                        val dialogB = MaterialAlertDialogBuilder(this)
                            .setTitle("Remove Bookmark")
                            .setMessage("Url:${it.binding.webView.url}")
                            .setPositiveButton("Remove"){self, _ ->
                                bookmarkList.removeAt(bookmarkIndex)
                                self.dismiss()}
                            .setNegativeButton("Cancel"){self, _ -> self.dismiss()}
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
            when{
                it.isCompleted -> Snackbar.make(binding.root, "Successful -> ${it.info.label}", 4000).show()
                it.isFailed -> Snackbar.make(binding.root, "Failed -> ${it.info.label}", 4000).show()
            }
        }
    }

    private fun saveAsPdf(web: WebView){
        val pm = getSystemService(Context.PRINT_SERVICE) as PrintManager

        val jobName = "${URL(web.url).host}_${
            SimpleDateFormat("HH:mm d_MMM_yy", Locale.ENGLISH)
            .format(Calendar.getInstance().time)}"
        val printAdapter = web.createPrintDocumentAdapter(jobName)
        val printAttributes = PrintAttributes.Builder()
        printJob = pm.print(jobName, printAdapter, printAttributes.build())
    }

    private fun changeFullscreen(enable: Boolean){
        if(enable){
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, binding.root).let { controller ->
//                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }else{
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, binding.root).show(WindowInsetsCompat.Type.systemBars())
        }
    }

    fun isBookmarked(url: String): Int{
        bookmarkList.forEachIndexed { index, bookmark ->
            if(bookmark.url == url) return index
        }
        return -1
    }

    fun saveBookmarks(){
        //for storing bookmarks data using shared preferences
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE).edit()

        val data = GsonBuilder().create().toJson(bookmarkList)
        editor.putString("bookmarkList", data)

        editor.apply()
    }

    private fun getAllBookmarks(){
        //for getting bookmarks data using shared preferences from storage
        bookmarkList = ArrayList()
        val editor = getSharedPreferences("BOOKMARKS", MODE_PRIVATE)
        val data = editor.getString("bookmarkList", null)

        if(data != null){
            val list: ArrayList<Bookmark> = GsonBuilder().create().fromJson(data, object: TypeToken<ArrayList<Bookmark>>(){}.type)
            bookmarkList.addAll(list)
        }
    }

}

@SuppressLint("NotifyDataSetChanged")
fun changeTab(url: String, fragment: Fragment, isBackground: Boolean = false){
    MainWebActivity.tabsList.add(Tab(name = url, fragment = fragment))
    myPager.adapter?.notifyDataSetChanged()
    tabsBtn.text = MainWebActivity.tabsList.size.toString()

    if(!isBackground) myPager.currentItem = MainWebActivity.tabsList.size - 1
}

fun checkForInternet(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

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