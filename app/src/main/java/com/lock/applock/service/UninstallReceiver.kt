package com.lock.applock.service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class UninstallReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_PACKAGE_REMOVED) {
            val packageName = intent.data?.schemeSpecificPart
            Log.d("UninstallReceiver", "Package removed: $packageName")
            // Perform your API call or other actions here
        }
    }}