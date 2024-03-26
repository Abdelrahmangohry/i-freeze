package com.lock.applock.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.database.ContentObserver
import android.os.Handler
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.lock.applock.R
import com.lock.applock.helper.NotificationHelper
import com.patient.data.cashe.PreferencesGateway

class LocationService : Service() {
    private lateinit var locationService: Intent
    private val helper by lazy { NotificationHelper(this) }
    var serviceApp: ForceCloseLocation? = null
    private var overlayView: View? = null
    lateinit var preferenc: PreferencesGateway

    private var windowManager: WindowManager? = null
    private var isServiceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as ForceCloseLocation.BinderForce
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
        locationService = Intent(applicationContext, ForceCloseLocation::class.java)
        bindService(locationService, serviceConnection, BIND_AUTO_CREATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferenc = PreferencesGateway(applicationContext)
        // Start monitoring location settings
        startLocationMonitoring()
        return START_STICKY
    }

    private fun startLocationMonitoring() {
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())
        // Check if location is enabled
        if (!isLocationEnabled()) {
            // Location is disabled, inflate layout
            Log.d("abdo", "Location Enabled from service should overlay")
            showOverlayLayout()
        } else {
            // Location is enabled, remove layout if present
            Log.d("abdo", "Location not Enabled from service should remove overlay")

            removeOverlayLayout()
        }
    }


    private fun isLocationEnabled(): Boolean {
        return try {
            val locationMode = Settings.Secure.getInt(
                contentResolver,
                Settings.Secure.LOCATION_MODE
            )
            locationMode != Settings.Secure.LOCATION_MODE_OFF
        } catch (e: Settings.SettingNotFoundException) {
            false
        }
    }

    private fun showOverlayLayout() {
        if (serviceApp != null) {
            serviceApp?.createChatHeadView()
        }
    }

    private fun removeOverlayLayout() {
        if (serviceApp != null) {
            serviceApp?.removeChatHeadView()
        }
        else{
            Log.d("abdo", "ServiceApp is null")
        }
    }

    override fun onDestroy() {
        removeOverlayLayout()
        if (isServiceBound) {
            unbindService(serviceConnection)
            isServiceBound = false
        }
        super.onDestroy()
    }
}