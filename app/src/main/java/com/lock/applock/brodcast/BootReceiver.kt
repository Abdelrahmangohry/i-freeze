package com.lock.applock.brodcast

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lock.applock.MainActivity


class BootReceiver: BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == Intent.ACTION_BOOT_COMPLETED){
            closeApps(p0)
            val intent =Intent(p0, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            p0?.startActivity(intent)
        }
    }

    private fun closeApps(context: Context?) {
        val appsToClose = listOf(
            "com.lock.applock",
            "com.example.app2")

        val am = context?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (packageName in appsToClose) {
            am.killBackgroundProcesses(packageName)
        }
    }

}