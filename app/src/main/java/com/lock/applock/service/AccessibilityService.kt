package com.lock.applock.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.lock.data.dp.AppsDB
import com.lock.data.model.AppsModel
import com.lock.di.RoomDBModule
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AccessibilityServices : AccessibilityService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var appsList: List<AppsModel>
    private val handler = Handler()
    lateinit var serviceIntent: Intent
    var serviceApp: ForceCloseService? = null
    private lateinit var preferenc: PreferencesGateway
    private fun isLauncherPackage(packageName: String): Boolean {
        val launcherPackages = listOf(
            "com.android.launcher", "com.google.android.launcher",
            "com.miui.home", "com.hihonor.android.launcher",
            "com.huawei.android.launcher", "com.sec.android.app.launcher",
            "com.samsung.android.app.launcher", "com.lock.applock"
        )
        return launcherPackages.any { packageName.startsWith(it) }
    }
    private fun getAppDatabaseInstance(): AppsDB {
        return RoomDBModule.provideRoomDB(applicationContext)
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        preferenc = PreferencesGateway(applicationContext)
        serviceIntent = Intent(applicationContext, ForceCloseService::class.java)
        serviceScope.launch {
            appsList = getAppDatabaseInstance().daoApps().getAppsList()
        }
        val packageName = p0?.packageName.toString()
        Log.d("islam", "packageName $packageName")
        if (p0?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            Log.d("islam", "packageName $packageName")
            if (!isLauncherPackage(packageName)) {
                handleAppBasedOnLists(packageName, serviceIntent)

            }
        }
    }

    fun killThisPackageIfRunning(context: Context, packageName: String?) {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(startMain)
        activityManager.killBackgroundProcesses(packageName)
    }

    private fun handleAppBasedOnLists(packageName: String, serviceIntent: Intent) {
        val isWhitelistEnabled = preferenc.load("Whitelist", false) ?: false
        val isBlacklistEnabled = preferenc.load("Blacklist", false) ?: false
        if (!::appsList.isInitialized){
            serviceScope.launch {
                appsList = getAppDatabaseInstance().daoApps().getAppsList()
            }
        }
        val isAppInWhitelist = isAppInList(packageName, appsList.filter { it.statusWhite == true })
        val isAppInBlacklist = isAppInList(packageName, appsList.filter { it.status == true })
        Log.d("islam", "isWhitelistEnabled $isWhitelistEnabled")
        Log.d("islam", "isBlacklistEnabled $isBlacklistEnabled")
        Log.d("islam", "isAppInBlacklist $isAppInBlacklist")
        if (isWhitelistEnabled) {
            if (isSystemApp(packageName)) {
                Log.d("islam", "Not white List ${isSystemApp(packageName)}")
                applicationContext.stopService(serviceIntent)
                removeOverlayAndViewBinding()
            } else if (!isAppInWhitelist) {
                Log.d("islam", "close applications Not white list ${isAppInWhitelist}")

                applicationContext.startService(serviceIntent)
                killAppAndShowOverlay(packageName)
            }
        } else if (isBlacklistEnabled && isAppInBlacklist) {
            Log.d("islam", "isAppInBlacklist ${isAppInBlacklist}")

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
        Log.d("islam", "onInterrupt: ")
    }
}