package com.lock.applock.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Handler
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.lock.data.dp.AppsDB
import com.lock.data.model.AppsModel
import com.lock.di.RoomDBModule
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class AccessibilityServices : AccessibilityService() {
    private  val TAG = "Mgd"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var appsList: List<AppsModel>
    private val handler = Handler()
    private lateinit var serviceIntent: Intent
    private var serviceApp: ForceCloseService? = null
    private lateinit var preferenc: PreferencesGateway
    private fun isLauncherPackage(packageName: String): Boolean {
        val launcherPackages = listOf(
            "com.android.launcher",
            "com.google.android.launcher",
            "com.miui.home",
            "com.hihonor.android.launcher",
            "com.huawei.android.launcher",
            "com.sec.android.app.launcher",
            "com.samsung.android.app.launcher",
            "com.lock.applock",
            "com.oppo.launcher",
            "com.coloros.launcher"
        )
        return launcherPackages.any { packageName.startsWith(it) }
    }

    private fun isBrowsers(packageName: String): Boolean {
        val browserList = listOf(
            "com.android.chrome",
            "org.mozilla.firefox",
            "com.microsoft.emmx",
            "com.opera.browser",
            "com.opera.browser.afin",
            "com.brave.browser",
            "com.sec.android.app.sbrowser",
            "com.UCMobile.intl"
        )
        return browserList.any { packageName.startsWith(it) }
    }

    //////////////
    private fun getAppDatabaseInstance(): AppsDB {
        return RoomDBModule.provideRoomDB(applicationContext)
    }
    ////////////////


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        preferenc = PreferencesGateway(applicationContext)

        // Create an intent for ForceCloseService class
        serviceIntent = Intent(applicationContext, ForceCloseService::class.java)

        // Use coroutine to asynchronously fetch the list of apps from the database
        serviceScope.launch {
            appsList = getAppDatabaseInstance().daoApps().getAppsList()
        }
        // Get the package name from the AccessibilityEvent
        val packageName = p0?.packageName.toString()
        Log.d(TAG, "packageName $packageName")

        // Check if the event type is a window state change
        if (p0?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d(TAG, "packageName $packageName")
            if (!isLauncherPackage(packageName)) {
                // Handle the app based on lists using the ForceCloseService intent
                handleAppBasedOnLists(packageName, serviceIntent)

            }
        }
    }

    private fun killThisPackageIfRunning(context: Context, packageName: String?) {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager

        // Create an intent to launch the home screen (main launcher)
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        // Set flags to start a new task and clear the existing task
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        // Start the home screen by launching the intent
        context.startActivity(startMain)
        activityManager.killBackgroundProcesses(packageName)
    }

    private fun handleAppBasedOnLists(packageName: String, serviceIntent: Intent) {
        val isWhitelistEnabled = preferenc.load("Whitelist", false) ?: false
        val isBlacklistEnabled = preferenc.load("Blacklist", false) ?: false
        val isBrowsersEnabled = preferenc.load("Browsers", false) ?: false
        if (!::appsList.isInitialized) {
            serviceScope.launch {
                appsList = getAppDatabaseInstance().daoApps().getAppsList()
            }
        }
        val isAppInBrowserList = isBrowsers(packageName)
        if (isBrowsersEnabled && isAppInBrowserList){
            applicationContext.startService(serviceIntent)
            killAppAndShowOverlay(packageName)
        }else{
            applicationContext.stopService(serviceIntent)
            removeOverlayAndViewBinding()
        }
        val isAppInWhitelist = isAppInList(packageName, appsList.filter { it.statusWhite == true })
        val isAppInBlacklist = isAppInList(packageName, appsList.filter { it.status == true })
        Log.d(TAG, "isWhitelistEnabled $isWhitelistEnabled")
        Log.d(TAG, "isBlacklistEnabled $isBlacklistEnabled")
        Log.d(TAG, "isAppInBlacklist $isAppInBlacklist")
        if (isWhitelistEnabled) {
            if (isSystemApp(packageName)) {
                Log.d(TAG, "Not white List ${isSystemApp(packageName)}")
                applicationContext.stopService(serviceIntent)
                removeOverlayAndViewBinding()
            } else if (!isAppInWhitelist) {
                Log.d(TAG, "close applications Not white list ${isAppInWhitelist}")

                applicationContext.startService(serviceIntent)
                killAppAndShowOverlay(packageName)
            }
        } else if (isBlacklistEnabled && isAppInBlacklist) {
            Log.d(TAG, "isAppInBlacklist ${isAppInBlacklist}")

            applicationContext.startService(serviceIntent)
            killAppAndShowOverlay(packageName)
        } else {
            applicationContext.stopService(serviceIntent)
            removeOverlayAndViewBinding()
        }
    }

    private fun isSystemApp(packageName: String): Boolean {
        val packageManager = applicationContext.packageManager
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    private fun removeOverlayAndViewBinding() {
        serviceApp?.removeChatHeadView()
    }

    private fun killAppAndShowOverlay(packageName: String) {
        killThisPackageIfRunning(applicationContext, packageName)
        serviceApp?.createChatHeadView()
        handler.postDelayed({ serviceApp?.removeChatHeadView() }, 5000)
    }


    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceApp = ForceCloseService()
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.notificationTimeout = 100
        this.serviceInfo = info
    }


    private fun isAppInList(packageName: String, list: List<AppsModel>): Boolean {
        return list.any { it.packageName == packageName }
    }

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt: ")
    }
}