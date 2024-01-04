package com.lock.applock.utils

import android.app.ActivityManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log

class utils(context: Context) {
    var usageStatsManager: UsageStatsManager? = null
    private val EXTRA_LAST_APP = "EXTRA_LAST_APP"
    private val LOCKED_APPS = "LOCKED_APPS"
    private val context: Context = context
    fun getLauncherTopApp(): String {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            val taskInfoList = manager.getRunningTasks(1)
            if (null != taskInfoList && !taskInfoList.isEmpty()) {
                return taskInfoList[0].topActivity!!.packageName
            }
        } else {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            var result = ""
            val event = UsageEvents.Event()
            val usageEvents: UsageEvents? = usageStatsManager?.queryEvents(beginTime, endTime)
            while (usageEvents?.hasNextEvent()==true) {
                usageEvents.getNextEvent(event)
                if (event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    result = event.packageName
                }
            }
            if (!TextUtils.isEmpty(result)) Log.d("RESULT", result)
            return result
        }
        return ""
    }

    fun getRunningApps(context: Context): List<String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageManager = context.packageManager
        val runningApps = mutableListOf<String>()

        // Get a list of running app processes
        val runningAppProcesses = activityManager.runningAppProcesses

        for (processInfo in runningAppProcesses) {
            try {
                // Get the application info for each running process
                val packageName = processInfo.processName
                val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)

                // Check if the app is not a system app
                if ((applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0) {
                    runningApps.add(packageName)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                // Handle exception if the app info is not found
                e.printStackTrace()
            }
        }

        return runningApps
    }
}

