package com.ifreeze.applock.service

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import com.ifreeze.applock.helper.NotificationHelper
import com.patient.data.cashe.PreferencesGateway

class KioskModeService : Service() {
    private lateinit var kioskModeService: Intent

    private val helper by lazy { NotificationHelper(this) }
    private var isOverlayShown = false

    var serviceApp: ForceCloseKiosk? = null

    lateinit var preferenc: PreferencesGateway

    private var windowManager: WindowManager? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as ForceCloseKiosk.BinderForce
            serviceApp = binder.getServices()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            // Consider handling service disconnection more robustly
            serviceApp = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        kioskModeService = Intent(applicationContext, ForceCloseKiosk::class.java)
        bindService(kioskModeService, serviceConnection, BIND_AUTO_CREATE)
        isServiceBound = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferenc = PreferencesGateway(applicationContext)
        // Start monitoring location settings
        monitorCurrentApp()
        return START_STICKY
    }


    @SuppressLint("ServiceCast")
    private fun monitorCurrentApp() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val handler = Handler(mainLooper)
        val runnable = object : Runnable {
            override fun run() {
                val blockState = preferenc.load("BlockState", false) as Boolean
                if (blockState) {
                    Log.d("kiosk", "BlockState is true")
                    val runningTasks = activityManager.runningAppProcesses
                    val topApp = runningTasks?.firstOrNull { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
                    topApp?.let {
                        val currentPackageName = it.processName
                        val whitelist = "com.facebook.katana"
                        if (currentPackageName == whitelist) {
                            if (isOverlayShown) {
                                removeOverlayLayout()
                                isOverlayShown = false
                                Log.d("kiosk", "App in whitelist, removed overlay")
                            }
                        } else {
                            if (!isOverlayShown) {
                                showOverlayLayout()
                                isOverlayShown = true
                                Log.d("kiosk", "App not in whitelist, showing overlay")
                            }
                        }
                    }
                } else {
                    Log.d("kiosk", "BlockState is false, stopping service")
                    stopSelf()
                }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runnable)
    }


    private fun showOverlayLayout() {
            serviceApp?.createChatHeadView()
    }

    private fun removeOverlayLayout() {
        serviceApp?.removeChatHeadView()
    }

    override fun onDestroy() {
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
        removeOverlayLayout()
        applicationContext.stopService(kioskModeService)

        super.onDestroy()
    }

}