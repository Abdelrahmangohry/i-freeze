package com.lock.applock.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.database.ContentObserver
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
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
    private var isOverlayShown = false

    var serviceApp: ForceCloseLocation? = null

    lateinit var preferenc: PreferencesGateway

    private var windowManager: WindowManager? = null
//    private var isServiceBound = false

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
//        preferenc = PreferencesGateway(applicationContext)
//        var locationToggle = preferenc.update("locationBlocked", isCheckedLocation)
        // Start the foreground service
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())

        // Get the location manager
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Define a provider status listener
        val providerListener = object : LocationListener {
            override fun onProviderEnabled(provider: String) {
                // Handle provider enabled
                if (isOverlayShown) {
                    removeOverlayLayout()
                    isOverlayShown = false // Reset the flag
                }
                Log.d("LocationStatus", "Location provider $provider enabled")
            }

            override fun onProviderDisabled(provider: String) {
                val locationStatus = preferenc.load("locationBlocked", false)
                Log.d("newwww", "locationStatus $locationStatus")
                // Handle provider disabled
                if (!isOverlayShown && preferenc.load("locationBlocked", false) == true) {
                    showOverlayLayout()
                    isOverlayShown = true // Set the flag
                }else{
                    removeOverlayLayout()
                    isOverlayShown = false
                }

                Log.d("LocationStatus", "Location provider $provider disabled")
            }

            override fun onLocationChanged(location: Location) {
                // Unused for provider status monitoring
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                // Unused for provider status monitoring
            }
        }

        // Register for location provider status updates
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,  // Update interval set to 0 for provider status changes only
                0f, // Minimum distance set to 0 for provider status changes only
                providerListener
            )
        } catch (e: SecurityException) {
            // Handle permission denied or other security-related exceptions
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
        } else {
            Log.d("abdo", "ServiceApp is null")
        }
    }

    override fun onDestroy() {
        removeOverlayLayout()
        applicationContext.stopService(locationService)

        super.onDestroy()
    }

}