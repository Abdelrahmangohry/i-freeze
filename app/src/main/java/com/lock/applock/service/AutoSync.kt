package com.lock.applock.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lock.applock.presentation.AuthViewModel
import com.lock.data.remote.UserApi
import com.patient.data.cashe.PreferencesGateway
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import java.net.UnknownHostException


@HiltWorker
class AutoSyncWorker @AssistedInject constructor(
    @Assisted private val api: UserApi,
    @Assisted context: Context,
    @Assisted workerParameter: WorkerParameters
) : CoroutineWorker(context, workerParameter) {
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")

    val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
//    val context = LocalContext.current
    override suspend fun doWork(): Result {
        while (true) {
            try {
                val response = api.newUpdateUserData(deviceId!!)
                if (response.isSuccessful) {
                    Log.d("abdo", "device id$deviceId")
                    Log.d("abdo", "result success")
                    Log.d("abdo", "Id: ${response.body()?.data}")
                    preference.update(
                        "Blacklist",
                        response.body()?.data?.device?.blockListApps!!
                    )
                    preference.update(
                        "Whitelist",
                        response.body()?.data?.device?.whiteListApps!!
                    )
                    preference.update(
                        "Browsers",
                        response.body()?.data?.device?.browsers!!
                    )
                    preference.update(
                        "WebBlacklist",
                        response.body()?.data?.device?.blockListURLs!!
                    )
                    preference.update(
                        "WebWhitelist",
                        response.body()?.data?.device?.whiteListURLs!!
                    )
                    preference.update(
                        "WifiBlocked",
                        response.body()?.data?.device?.blockWiFi!!
                    )

                    if (response.body()?.data?.device?.blockWiFi!!) {
                        applicationContext.startService(serviceIntent)
                    } else {
                        applicationContext.stopService(serviceIntent)
                    }
                    preference.update(
                        "WifiWhite",
                        response.body()?.data?.device?.whiteListWiFi!!
                    )
                    preference.save("time", response.body()?.data?.device?.time!!)
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
            delay(10*60*10000 ) // Wait for 10 seconds before next iteration
        }
    }

}
    //    private fun clickSyncButton() {
//        val deviceId = preference.load("responseID", "")
//        val serviceIntent = Intent(this, NetworkMonitoringService::class.java)
//        if (!isNetworkAvailable(this)) {
//            Toast.makeText(
//                this,
//                "Please connect to the internet",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else if (deviceId.isNullOrEmpty()) {
//            Toast.makeText(
//                this,
//                "Enter a Valid Key First",
//                Toast.LENGTH_SHORT
//            ).show()
//        } else {
//            viewModel.newUpdateUserData(deviceId)
//            viewModel._newFlow.observe(this, Observer { responseId ->
//                if (responseId.isSuccessful) {
//                    Log.d("abdo", "new response query ${responseId.body()?.data}")
//                    Toast.makeText(
//                        this,
//                        "Data Synchronized Successfully",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                } else {
//                    Log.d("abdo", "kolo error ${responseId.message()}")
//                    Toast.makeText(
//                        this,
//                        "Failed to Synchronize",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            })
//        }
//    }

