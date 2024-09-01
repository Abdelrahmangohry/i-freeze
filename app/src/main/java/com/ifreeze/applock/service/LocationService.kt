package com.ifreeze.applock.service

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import com.ifreeze.applock.helper.NotificationHelper
import com.ifreeze.data.cash.PreferencesGateway

/**
 * A service that monitors location settings and manages an overlay layout based on location provider status.
 */
class LocationService : Service() {
    private lateinit var locationService: Intent

    private val helper by lazy { NotificationHelper(this) }
    private var isOverlayShown = false

    var serviceApp: ForceCloseLocation? = null

    lateinit var preferenc: PreferencesGateway

    private var windowManager: WindowManager? = null

    /**
     * Called when a connection to the ForceCloseLocation service is established.
     *
     * @param componentName The name of the service component that has been connected.
     * @param iBinder The IBinder instance provided by the connected service.
     */
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as ForceCloseLocation.BinderForce
            serviceApp = binder.getServices()
        }

        /**
         * Called when the connection to the ForceCloseLocation service is lost.
         *
         * @param componentName The name of the service component that was disconnected.
         */
        override fun onServiceDisconnected(p0: ComponentName?) {
            // Consider handling service disconnection more robustly
            serviceApp = null
        }
    }


    /**
     * Called when a client binds to this service. This service does not provide binding, so returns null.
     *
     * @param intent The Intent that was used to bind to this service.
     * @return null since this service does not support binding.
     */
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /**
     * Called when the service is created. Initializes the service and binds to the ForceCloseLocation service.
     */
    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        locationService = Intent(applicationContext, ForceCloseLocation::class.java)
        bindService(locationService, serviceConnection, BIND_AUTO_CREATE)
    }


    /**
     * Called when the service is started. Begins monitoring location settings.
     *
     * @param intent The Intent that started this service.
     * @param flags Additional data about the service start request.
     * @param startId An identifier for this specific start request.
     * @return START_STICKY to indicate that the service should be restarted if it is killed.
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        preferenc = PreferencesGateway(applicationContext)
        // Start monitoring location settings
        startLocationMonitoring()
        return START_STICKY
    }


    /**
     * Starts monitoring the location provider status and manages the overlay layout based on provider status.
     */
    private fun startLocationMonitoring() {
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


    /**
     * Displays the overlay layout by calling the createChatHeadView method on the serviceApp.
     */
    private fun showOverlayLayout() {
        if (serviceApp != null) {
            serviceApp?.createChatHeadView()
        }
    }

    /**
     * Removes the overlay layout by calling the removeChatHeadView method on the serviceApp.
     */
    private fun removeOverlayLayout() {
        if (serviceApp != null) {
            serviceApp?.removeChatHeadView()
        } else {
            Log.d("abdo", "ServiceApp is null")
        }
    }

    /**
     * Called when the service is destroyed. Stops the location service and removes the overlay layout.
     */
    override fun onDestroy() {
        removeOverlayLayout()
        applicationContext.stopService(locationService)

        super.onDestroy()
    }
}