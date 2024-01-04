package com.lock.applock.service

import android.app.Notification
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.content.getSystemService
import com.lock.applock.helper.NotificationHelper
import com.patient.data.cashe.PreferencesGateway

class NetworkMonitoringService : Service() {
    lateinit var serviceIntent: Intent

    // Helper for managing notifications
    private val helper by lazy { NotificationHelper(this) }
    var serviceApp: ForceCloseWifi? = null
    lateinit var preferenc: PreferencesGateway
    private lateinit var wifiManager: WifiManager


    // Handles the connection with the ForceCloseWifi service
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as ForceCloseWifi.BinderForce
            serviceApp = binder.getServices()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            // Consider handling service disconnection more robustly
        }
    }

    override fun onCreate() {
        // Use class name for logging
        Log.d(javaClass.simpleName, "NetworkMonitoringService onCreate: ")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        serviceIntent = Intent(applicationContext, ForceCloseWifi::class.java)
        preferenc = PreferencesGateway(applicationContext)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        startForegroundService()
        return START_STICKY
    }

    override fun onDestroy() {
        applicationContext.stopService(serviceIntent)
        super.onDestroy()

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    // Starts the foreground service and registers network callback
    private fun startForegroundService() {
        startForeground(NotificationHelper.NOTIFICATION_ID, helper.getNotification())

        // Consider extracting network monitoring to a separate method or class
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Handle network available event
                Log.d("islam", "onAvailable: ")
                showNetworkInfo(connectivityManager.activeNetworkInfo)
            }

            override fun onLost(network: Network) {
                // Handle network lost event
                Log.d("islam", "onLost: ")
                showNetworkInfo(connectivityManager.activeNetworkInfo)
                applicationContext.stopService(serviceIntent)
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun getCurrentSSID(): String {
        if (wifiManager.isWifiEnabled) {
            val wifiInfo: WifiInfo? = wifiManager.connectionInfo
            if (wifiInfo != null && wifiInfo.networkId != -1) {
                return wifiInfo.ssid.replace("\"", "")
            }
        }
        return "Not connected"
    }

    // Displays network information in a notification
    private fun showNetworkInfo(networkInfo: NetworkInfo?) {
        // Handle the network info here
        if (networkInfo != null) {
            val networkType = when (networkInfo.type) {
                ConnectivityManager.TYPE_WIFI -> {
                    val ssid = "flothers"
                    val list: ArrayList<String> = preferenc.getList("allowedWifiList")
//                    var mac = getMacAddress()

                    Log.d("islam", "showNetworkInfo : ${preferenc.load("WifiBlocked", false)} ")
                    if (preferenc.load("WifiBlocked", false) == true) {
                        startService()
                        //      Log.d("islam", "getCurrentSSID : ${getCurrentSSID().contains(ssid, true)} ")
                    }
                    if (preferenc.load("WifiWhite", false) == true) {

                          if (!list.contains(getCurrentSSID()))
                               startService()
                        else {
                              return
                          }

                    }
                    "Wi-Fi"
                }

                ConnectivityManager.TYPE_MOBILE -> {
                    "Mobile Data"
                }

                else -> "Other"
            }

            val networkDetails = "Network Type: $networkType\n" +
                    "Is Connected: ${networkInfo.isConnected}\n" +
                    "Extra Info: ${networkInfo.extraInfo}"
            helper.updateNotification(networkDetails)
        }
    }

    // Binds the ForceCloseWifi service
    fun startService() {
        // Simplify the version check and service binding logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(
                applicationContext
            )
        ) {
            applicationContext.startService(serviceIntent)
//            bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    companion object {
        // Consider moving these constants to a separate file for better organization
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "NetworkMonitoringChannel"
    }

//    private fun getMacAddress(): String {
//        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//        val wInfo: WifiInfo = wifiManager.connectionInfo
//        val macAddress: String = wInfo.macAddress
//
//        return macAddress
//    }
}
