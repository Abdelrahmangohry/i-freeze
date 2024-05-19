package com.ifreeze.applock.service

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
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class AccessibilityServices : AccessibilityService() {
    private val TAG = "Mgd"
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var blockedAppList: List<String>
    private lateinit var allowedAppsList: List<String>

    private val handler = Handler()
    lateinit var serviceIntent: Intent
    var serviceApp: ForceCloseService? = null
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
            "com.ifreeze.applock",
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
            "com.brave.browser",
            "com.sec.android.app.sbrowser",
            "com.UCMobile.intl"
        )
        return browserList.any { packageName.startsWith(it) }
    }

    //////////////
//    private fun getAppDatabaseInstance(): AppsDB {
//        return RoomDBModule.provideRoomDB(applicationContext)
//    }
    ////////////////


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        preferenc = PreferencesGateway(applicationContext)

        // Create an intent for ForceCloseService class
        serviceIntent = Intent(applicationContext, ForceCloseService::class.java)

        blockedAppList = preferenc.getList("blockedAppsList")
        Log.d("abdo", "this issssss blockedAppList $blockedAppList")

        allowedAppsList = preferenc.getList("allowedAppsList")

        // Get the package name from the AccessibilityEvent
        val packageName = p0?.packageName.toString()
        Log.d("abdo", "this issssss packageName $packageName")

        // Check if the event type is a window state change
        if (p0?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("abdo", "packageName $packageName")
            if (!isLauncherPackage(packageName)) {
//                 Handle the app based on lists using the ForceCloseService intent
                handleAppBasedOnLists(packageName, serviceIntent)

            }
        }
    }

    fun killThisPackageIfRunning(context: Context, packageName: String?) {
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
        serviceScope.launch {
            // Ensure appsList is initialized before proceeding
            blockedAppList = preferenc.getList("blockedAppsList")
            Log.d("abdo", " blockedAppList $blockedAppList")
            allowedAppsList = preferenc.getList("allowedAppsList")
            val isWhitelistEnabled = preferenc.load("Whitelist", false) ?: false
            val isBlacklistEnabled = preferenc.load("Blacklist", false) ?: false
            val isBrowsersEnabled = preferenc.load("Browsers", false) ?: false
//        if (!::blockedAppList.isInitialized){
//            serviceScope.launch {
//                blockedAppList = preferenc.getList("blockedAppsList")
//            }
//        }
            val isAppInBrowserList = isBrowsers(packageName)
            if (isBrowsersEnabled && isAppInBrowserList) {
                applicationContext.startService(serviceIntent)
                killAppAndShowOverlay(packageName)
            } else {
                applicationContext.stopService(serviceIntent)
                removeOverlayAndViewBinding()
            }
            val isAppInWhitelist = isAppInList(packageName, allowedAppsList)
            val isAppInBlacklist = isAppInList(packageName, blockedAppList)

            Log.d("abdo", "isWhitelistEnabled $isWhitelistEnabled")

            Log.d("abdo", "isWhitelistEnabled $isWhitelistEnabled")
            Log.d("abdo", "isBlacklistEnabled $isBlacklistEnabled")
//        Log.d("islam", "isAppInBlacklist $isAppInBlacklist")
            if (isWhitelistEnabled) {
                if (isSystemApp(packageName)) {
                    Log.d("abdo", "this is system app ${isSystemApp(packageName)}")
                    applicationContext.stopService(serviceIntent)
                    removeOverlayAndViewBinding()
                } else if (!isAppInWhitelist) {
                    Log.d("abdo", "else is white list enabled")
                    applicationContext.startService(serviceIntent)
                    killAppAndShowOverlay(packageName)
                }
            } else if (isBlacklistEnabled && isAppInBlacklist) {
                Log.d("abdo", "blacklist enabled")
                applicationContext.startService(serviceIntent)
                killAppAndShowOverlay(packageName)
            } else {
                applicationContext.stopService(serviceIntent)
                removeOverlayAndViewBinding()
            }
        }
    }

    private fun isSystemApp(packageName: String): Boolean {
        val packageManager = applicationContext.packageManager
        Log.d("abdo", "system app 0 $packageManager")

        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            Log.d("abdo", "system app 1 $packageInfo")
            return packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, "isSystemApp: $e")
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
        val info = AccessibilityServiceInfo()
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN
        info.notificationTimeout = 100
        this.serviceInfo = info
    }

    private fun isAppInList(packageName: String, list: List<String>): Boolean {
        var isFound = false;
        for (item in list) {
            if (packageName.contains(item)) {
                isFound = true
                break
            }
        }
        return isFound
    }

    override fun onInterrupt() {
        Log.d("islam", "onInterrupt: ")
    }
}