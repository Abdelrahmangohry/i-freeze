package com.lock.applock.service

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.work.Worker
import androidx.work.WorkerParameters


class PeriodicAppMonitorWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("islam worker", "doWork: ")
        checkOverlayPermission()
        startService()

        return Result.success()
    }
    fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(applicationContext)) {
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                applicationContext.startActivity(myIntent)
            }
        }
    }
    fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(applicationContext)) {
                // start the service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val serviceIntent = Intent(applicationContext, ForceCloseService::class.java)
                    applicationContext.startService(serviceIntent)
                } else {
                    val serviceIntent = Intent(applicationContext, ForceCloseService::class.java)
                    applicationContext.startService(serviceIntent)
                }
            }
        } else {
            val serviceIntent = Intent(applicationContext, ForceCloseService::class.java)
            applicationContext.startService(serviceIntent)
        }
    }

}