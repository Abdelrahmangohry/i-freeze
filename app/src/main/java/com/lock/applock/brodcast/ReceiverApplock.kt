package com.lock.applock.brodcast

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.lock.applock.utils.utils
import com.patient.data.cashe.PreferencesGateway
import java.time.LocalDate
import java.util.Calendar

class ReceiverApplock : BroadcastReceiver() {
    private lateinit var currentDay: Calendar
    override fun onReceive(p0: Context?, p1: Intent?) {
        val utils = p0?.let { utils(it) }
        val preferenc = p0?.let { PreferencesGateway(it) }
        val lockedApps = preferenc?.getLockedAppsList()
        val appRunning = utils?.getLauncherTopApp()
        Log.d("islam", "onReceive data : ${preferenc?.getLockedAppsList()} ")
        Log.d("islam", "onReceive current  : ${utils?.getRunningApps(p0)} ")

        if (lockedApps?.contains(appRunning) == true) {
            preferenc.remove("EXTRA_LAST_APP")
            preferenc.setLastApp(appRunning)
            if (appRunning != null) {
                killThisPackageIfRunning(p0, appRunning)
            }
//            val i = Intent(p0, ScreenBlockerActivity::class.java)
//            i.flags =
//                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            i.putExtra("broadcast_receiver", "broadcast_receiver")
//            p0.startActivity(i)
        }
    }


    companion object {
        fun killThisPackageIfRunning(context: Context, packageName: String) {
            val activityManager =
                context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val startMain = Intent(Intent.ACTION_MAIN)
            startMain.addCategory(Intent.CATEGORY_HOME)
            startMain.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(startMain)
            activityManager.killBackgroundProcesses(packageName)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun checkDay(weekdays: List<String>): Boolean {
        currentDay = Calendar.getInstance()
        val today = LocalDate.now().dayOfWeek.name
        return weekdays.contains(today)
    }
}