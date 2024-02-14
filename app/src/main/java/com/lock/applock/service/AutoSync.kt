package com.lock.applock.service

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.lock.data.model.LocationData
import com.lock.data.model.LocationDataAddress
import com.lock.data.model.LocationModel
import com.lock.data.remote.UserApi
import com.lock.data.repo.auth.LocationRepository
import com.lock.data.repo.auth.LocationViewModel
import com.patient.data.cashe.PreferencesGateway
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.UnknownHostException


class AutoSyncWorker @AssistedInject constructor(
    @Assisted private val api: UserApi,
    @Assisted context: Context,
    @Assisted workerParameter: WorkerParameters,

) : CoroutineWorker(context, workerParameter) {
    private val preference = PreferencesGateway(context)
    private val deviceId = preference.load("responseID", "")
    private val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
//    private val userLocation = LocationModel(
//        location = "Giza",
//        deviceId = deviceId!!
//    )

    override suspend fun doWork(): Result {
        try {
            // Use the fetched address or default to "Giza"
            val userLocation = LocationModel(
                location = "Giza", // Use fetched address or default
                deviceId = deviceId!!
            )

            val response = api.newUpdateUserData(deviceId!!)
            val userLocationResponse = api.userLocation(userLocation)
            if (userLocationResponse.isSuccessful) {
                Log.d("abdo", "important thing ${userLocationResponse.body()}")
            }
            if (response.isSuccessful) {
                Log.d("abdo", "device id $deviceId")
                Log.d("abdo", "result success")
                Log.d("abdo", "Id: ${response.body()?.data}")
                val responseData = response.body()?.data?.device
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
                Log.d("abdo", "retrying....")
            }
        } catch (e: Exception) {
            if (e is UnknownHostException) {
                Log.d("abdo", "retrying....")
            } else {
                Log.d("abdo", "Error")
                return Result.failure(Data.Builder().putString("error", e.toString()).build())
            }
        }
        return Result.success()
    }
}


