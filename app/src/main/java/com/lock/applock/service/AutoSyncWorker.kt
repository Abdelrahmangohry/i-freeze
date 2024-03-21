package com.lock.applock.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.toLowerCase
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.lock.data.model.LocationDataAddress
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.remote.UserApi
import com.lock.data.repo.auth.LocationHelper
import com.patient.data.cashe.PreferencesGateway
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

@HiltWorker
class AutoSyncWorker @AssistedInject constructor(
    @Assisted private val api: UserApi,
    @Assisted context: Context,
    @Assisted workerParameter: WorkerParameters,
) : CoroutineWorker(context, workerParameter), LocationHelper.LocationCallback {

    private val preference = PreferencesGateway(context)
    private val allowedList = preference.getList("allowedWifiList")
    private val blockedWebsites = preference.getList("blockedWebsites")

    private val deviceId = preference.load("responseID", "")
    private val serviceIntent = Intent(context, NetworkMonitoringService::class.java)

    private val installedAppsList = getInstalledApps(context)

    init {
        LocationHelper.getLocation(context, this)
    }

    override suspend fun onLocationFetched(locationData: LocationDataAddress) {
        try {
            val address = locationData.address ?: "Unknown Address"
            val latitude = locationData.latitude ?: 0.0
            val longitude = locationData.longitude ?: 0.0
            val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")

            val userLocation = LocationModel(
                location = mapUri.toString(),
                address = address,
                deviceId = deviceId!!
            )


            val response = api.newUpdateUserData(deviceId)
            val userLocationResponse = api.userLocation(userLocation)

            val mobileApplication = MobileApps(
                deviceId = deviceId,
                appName = installedAppsList
            )
            val mobileApplications = api.mobileApps(mobileApplication)
            if (mobileApplications.isSuccessful) {
                Log.d("abdo", "mobile application sent successfully")
            }

            if (userLocationResponse.isSuccessful) {
                Log.d("abdo", "User location updated successfully")
            }
            if (response.isSuccessful) {
                val cloudList = response.body()?.data?.exceptionWifi
                val newList = ArrayList<String>().apply {
                    addAll(allowedList ?: emptyList())
                    cloudList?.forEach{it
                        if (it !in allowedList.orEmpty()) {
                            add(it.toLowerCase().trim())
                        }
                    }
                }

                Log.d("abdo", "newList $newList")
                preference.saveList("allowedWifiList", newList)
                Log.d("abdo", "Cloud listssss: $cloudList")


                val cloudBlockedWebSites = response.body()?.data?.blockedWebsites
                val newBlockedWebSites = ArrayList<String>().apply {
                    addAll(blockedWebsites)
                    cloudBlockedWebSites?.forEach {it
                        if (it !in blockedWebsites){
                            add(it.toLowerCase().trim())
                        }
                    }
                }
                preference.saveList("blockedWebsites", newBlockedWebSites)

                Log.d("abdo", "Result success")

                val responseData = response.body()?.data?.device
                val blockedAppsList = response.body()?.data?.blockedApps
                if (blockedAppsList != null) { // Check for null
                    preference.saveList("blockedAppsList", blockedAppsList)
                    Log.d("abdo", "this is blocked apps list $blockedAppsList")
                } else {
                    Log.d("abdo", "Blocked apps list is null")
                }
                var allowedAppsList = response.body()?.data?.exceptionApps
                if (allowedAppsList != null) { // Check for null
                    preference.saveList("allowedAppsList", allowedAppsList)
                    Log.d("abdo", "this is blocked apps list $allowedAppsList")
                } else {
                    Log.d("abdo", "Blocked apps list is null")
                }
                responseData?.let {
                    preference.update("Blacklist", it.blockListApps)
                    preference.update("Whitelist", it.whiteListApps)
                    preference.update("Browsers", it.browsers)
                    preference.update("WebBlacklist", it.blockListURLs)
                    preference.update("WebWhitelist", it.whiteListURLs)
                    preference.update("WifiBlocked", it.blockWiFi)
                    if (it.blockWiFi) {
                        applicationContext.startService(serviceIntent)
                    } else {
                        applicationContext.stopService(serviceIntent)
                    }
                    preference.update("WifiWhite", it.whiteListWiFi)
                    preference.save("time", it.time)
                }
            } else {
                Log.d("abdo", "Retrying....")
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.d("abdo", "Retrying....")
            } else {
                Log.e("abdo", "Error", e)
//                val errorData = Data.Builder().putString("error", e.toString()).build()
//                Result.failure(errorData)
            }
        }
    }

    override suspend fun doWork(): Result {
        // This method will be invoked by WorkManager
        // We don't do anything here because we initiate the work in the constructor
        return Result.success()
    }

}

fun startAutoSyncWorker(context: Context) {
    // Create a periodic work request for AutoSyncWorker
    val workRequest = PeriodicWorkRequestBuilder<AutoSyncWorker>(360, TimeUnit.MINUTES)
        .setInitialDelay(1, TimeUnit.SECONDS)
        .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.SECONDS)
        .build()

    // Enqueue the periodic work request
    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
        "AutoSync",
        ExistingPeriodicWorkPolicy.REPLACE,
        workRequest
    )
}

fun getInstalledApps(context: Context): List<String> {
    val packageManager: PackageManager = context.packageManager
    val installedApplications =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

    val appNames = mutableListOf<String>()
    for (appInfo in installedApplications) {
        appNames.add(appInfo.packageName)
    }
    return appNames
}