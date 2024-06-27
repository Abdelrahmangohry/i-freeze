package com.ifreeze.applock.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ifreeze.applock.presentation.activity.areDeveloperOptionsEnabled
import com.ifreeze.applock.presentation.activity.hasLockScreenPassword
import com.ifreeze.applock.presentation.activity.isDeviceRooted
import com.ifreeze.applock.service.AutoSyncWorker.Companion.hashesList
import com.ifreeze.data.model.AlertBody
import com.ifreeze.data.model.LocationDataAddress
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.ProactiveResultsBody
import com.ifreeze.data.remote.UserApi
import com.ifreeze.data.repo.auth.LocationHelper
import com.ifreeze.di.NetWorkModule

import com.patient.data.cashe.PreferencesGateway
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject

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
    private val kioskIntent = Intent(context, ForceCloseKiosk::class.java)
    private val locationService = Intent(context, LocationService::class.java)
    private val installedAppsList = getInstalledApps(context)
    val hashesListDatabase = preference.getList("hashesListDatabase")
    private var failureCount = preference.load("failureCount", 0)
    private var isFailureLimitReached = preference.load("isFailureLimitReached", false)
    private val licenseID = preference.load("licenseID", "")
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    ///
    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    val checkItems = mutableListOf<Pair<String, Boolean>>()
    val lockedScreen = lockScreen(context)
    val rooted = deviceRootedEnable()
    val developerOptionsEnabled = developerOptionsEnabled(context)
    ///
    val downloadDirectory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    companion object {
        val hashesList = mutableListOf<Pair<String, String>>()
    }

    init {
        failureCount = preference.load("failureCount", 0)!!
        LocationHelper.getLocation(context, this)
    }


    override suspend fun onLocationFetched(locationData: LocationDataAddress) {
        try {

            val baseUrl = "https://security.flothers.com:8443/api/"
//            val getNewBaseUrl = api.getCloudURL(deviceId!!)
//            if (getNewBaseUrl.isSuccessful){
//                val updatedUrl = getNewBaseUrl.body()?.data?.url
//                Log.d("server", "updatedUrl from auto sync $updatedUrl")
//                preference.saveBaseUrl(updatedUrl!!)
//            }
            preference.saveBaseUrl(baseUrl)

            if (failureCount!! >= 120) {
                isFailureLimitReached = true
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }
            else{
                isFailureLimitReached = false
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }

            if (
                !Settings.canDrawOverlays(applicationContext) ||
                enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true ||
                ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    applicationContext,
                    "Please enable i-Freeze permissions in app permissions",
                    Toast.LENGTH_SHORT
                )
                    .show()

            }
//            val kioskApplications = api.getKioskApps()
//            if(kioskApplications.isSuccessful){
//
//            }


            val address = locationData.address ?: "Unknown Address"
            val latitude = locationData.latitude ?: 0.0
            val longitude = locationData.longitude ?: 0.0
            val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")

            val userLocation = LocationModel(
                location = mapUri.toString(),
                address = address,
                deviceId = deviceId!!
            )


            val userLocationResponse = api.userLocation(userLocation)

            val mobileApplication = MobileApps(
                deviceId = deviceId,
                appName = installedAppsList
            )

            val checklicenseData = api.checkLicenseData(licenseID!!)
            if (checklicenseData.isSuccessful){
                val responseBody = checklicenseData.body()?.toString()
                val isLicenseValid = responseBody?.toBoolean() ?: false // Convert string to boolean
                preference.save("validLicense", isLicenseValid)
                Log.d("abdo", "Is license valid: $isLicenseValid")
            }

            val mobileApplications = api.mobileApps(mobileApplication)
            if (mobileApplications.isSuccessful) {
                Log.d("abdo", "mobile application sent successfully")
                failureCount = 0
                Log.d("abdo", "failureCount from try $failureCount")
                preference.save("failureCount", failureCount!!)
                Log.d("abdo", "isFailureLimitReached from try $isFailureLimitReached")
            }

            if (userLocationResponse.isSuccessful) {
                Log.d("abdo", "User location updated successfully")
            }


            val response = api.newUpdateUserData(deviceId)
            if (response.isSuccessful) {
                val applicationNamesList = response.body()?.data?.deviceKioskApps ?: emptyList()
                preference.saveList("kioskApplications", applicationNamesList)
                Log.d("kioskauto", "kiosk applications $applicationNamesList")


                val cloudList = response.body()?.data?.exceptionWifi
                val newList = ArrayList<String>().apply {
                    addAll(allowedList ?: emptyList())
                    cloudList?.forEach {
                        it
                        if (it !in allowedList.orEmpty()) {
                            add(it.toLowerCase().trim())
                        }
                    }
                }
                preference.saveList("allowedWifiList", newList)

                val cloudAllowedWebSites = response.body()?.data?.exceptionWebsites
                if (cloudAllowedWebSites != null) {
                    preference.saveList("allowedWebsites", cloudAllowedWebSites)
                }

                val cloudBlockedWebSites = response.body()?.data?.blockedWebsites
                Log.d("allowed", "cloudBlockedWebSites $cloudBlockedWebSites")
//                val newListCloudBlockedWebSites = ArrayList<String>().apply {
//                    addAll(blockedWebsites ?: emptyList())
//                    cloudBlockedWebSites?.forEach {
//                        it
//                        if (it !in blockedWebsites.orEmpty()) {
//                            add(it.toLowerCase().trim())
//                        }
//                    }
//                }
                if (cloudBlockedWebSites != null) {
                preference.saveList("blockedWebsites", cloudBlockedWebSites)
                }
                val responseData = response.body()?.data?.device
                val blockedAppsList = response.body()?.data?.blockedApps
                if (blockedAppsList != null) { // Check for null
                    preference.saveList("blockedAppsList", blockedAppsList)
                    Log.d("abdo", "this is blocked apps list $blockedAppsList")
                }

                val allowedAppsList = response.body()?.data?.exceptionApps
                if (allowedAppsList != null) { // Check for null
                    preference.saveList("allowedAppsList", allowedAppsList)
                    Log.d("allowed", "this is allowe apps list $allowedAppsList")
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
                    preference.update("BlockState", it.kiosk)
                    preference.update("locationBlocked", it.kiosk)
                    if (it.kiosk) {
                        applicationContext.startService(kioskIntent)
                    } else {
                        applicationContext.stopService(kioskIntent)
                    }
                    if (it.blockWiFi) {
                        applicationContext.startService(serviceIntent)
                    } else {
                        applicationContext.stopService(serviceIntent)
                    }
                    preference.update("WifiWhite", it.whiteListWiFi)
                    preference.update("locationBlocked", it.locationTracker)
                    if (it.locationTracker) {
                        applicationContext.startService(locationService)
                    } else {
                        applicationContext.stopService(locationService)
                    }
                    preference.save("time", it.time)

                }
            }
            else {
                Log.d("abdo", "Retrying.... before ")
                applicationContext.startService(serviceIntent)
            }

            if (!lockedScreen) {
                checkItems.add("Lock Screen" to true)
            } else {
                checkItems.add("There Is No Lock Screen" to false)
            }
            checkItems.add("Rooted Device" to rooted)
            if (developerOptionsEnabled) {
                checkItems.add("Developer options are enabled" to true)
            } else {
                checkItems.add("Developer Option Disabled" to false)
            }
            checkItems.filter { it.second }.forEach { (issueName, _) ->
                val message : List<AlertBody> = listOf(AlertBody(
                    deviceId = deviceId,
                    logName = issueName,
                    time = currentTime,
                    action = "String",
                    description = "$issueName Found",
                    source = "Settings Alerts"
                ))
                val alertIssues = api.sendAlert(message)
                if (alertIssues.isSuccessful) {
                    Log.d("abdo", "mobile Issues Sent Successfully")

                }
            }
            getHashCodeFromFiles(downloadDirectory)

            // Compare hashesList with hashesListDatabase
            val matchedHashes = hashesList.filter { hashPair ->
                hashesListDatabase?.contains(hashPair.second) == true
            }

            // If there are matches, send the proactive result
            if (matchedHashes.isNotEmpty()) {
                val messagePro: List<ProactiveResultsBody> = matchedHashes.map { matchedHash ->
                    ProactiveResultsBody(
                        deviceId = deviceId,
                        processName = matchedHash.first, // file path as processName
                        time = currentTime,
                        severity = "High",
                        source = "Proactive Scan"
                    )
                }

                val proActiveResult = api.sendProactiveResults(messagePro)
                if (proActiveResult.isSuccessful) {
                    Log.d("abdo", "Proactive result sent successfully")
                }
            }



        } catch (e: Exception) {

            Log.e("abdo", "Error", e)
            failureCount = failureCount!! + 1
            preference.save("failureCount", failureCount!!)
            if (failureCount!! >= 20) {
                isFailureLimitReached = true
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }else{
                isFailureLimitReached = false
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }
            applicationContext.startService(serviceIntent)
//                val errorData = Data.Builder().putString("error", e.toString()).build()
//                Result.failure(errorData)
            Log.d("abdo", "failureCount from error $failureCount")
            Log.d("abdo", "isFailureLimitReached $isFailureLimitReached")

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

@SuppressLint("SuspiciousIndentation")
fun getInstalledApps(context: Context): List<String> {
    val apps = mutableListOf<String>()
    val pk = context.packageManager
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    val resolveInfoList = pk.queryIntentActivities(intent, 0)
    for (resolveInfo in resolveInfoList) {
        val activityInfo = resolveInfo.activityInfo
        val name = activityInfo.loadLabel(pk).toString()
        val packageName = activityInfo.packageName
        apps.add(name)
    }
    return apps
}


fun lockScreen(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}


fun deviceRootedEnable(): Boolean {
    return try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "ls /data"))
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
        val output = StringBuilder()
        var line: String?
        while (bufferedReader.readLine().also { line = it } != null) {
            output.append(line)
        }
        process.waitFor()
        output.toString().isNotEmpty()
    } catch (e: Exception) {
        false
    }
}


fun developerOptionsEnabled(context: Context): Boolean {
    return Settings.Secure.getInt(
        context.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0
}

private suspend fun getHashCodeFromFiles(directory: File) {
    withContext(Dispatchers.IO) {
        if (directory.exists() && directory.canRead()) {
            val hashes = mutableListOf<Pair<String, String>>()
            scanDirectory(directory, hashes)
            // Log each file path and its hash
            hashes.forEachIndexed { index, pair ->
                Log.d("HashLog", "File ${index + 1}: Path: ${pair.first}, Hash: ${pair.second}")
                hashesList.add(pair)
            }
        } else {
            Log.d("HashLog", "Cannot access directory.")
        }
    }
}


private fun scanDirectory(directory: File, hashes: MutableList<Pair<String, String>>) {
    if (directory.isDirectory) {
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                scanDirectory(file, hashes)
            } else {
                val hash = getFileHash(file)
                hashes.add(Pair(file.absolutePath, hash))
            }
        }
    } else {
        Log.d("HashLog", "${directory.absolutePath} is not a directory.")
    }
}

private fun getFileHash(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val fis = FileInputStream(file)
    val byteArray = ByteArray(1024)
    var bytesCount: Int

    while (fis.read(byteArray).also { bytesCount = it } != -1) {
        digest.update(byteArray, 0, bytesCount)
    }
    fis.close()

    val hashedBytes = digest.digest()

    // Convert byte array to hexadecimal string
    val stringBuilder = StringBuilder()
    for (byte in hashedBytes) {
        stringBuilder.append(String.format("%02x", byte))
    }

    return stringBuilder.toString()
}
