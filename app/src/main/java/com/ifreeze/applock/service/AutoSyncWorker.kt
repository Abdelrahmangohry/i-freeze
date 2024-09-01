package com.ifreeze.applock.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.ifreeze.applock.service.AutoSyncWorker.Companion.hashesList
import com.ifreeze.data.model.AlertBody
import com.ifreeze.data.model.LocationDataAddress
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.ProactiveResultsBody
import com.ifreeze.data.remote.UserApi
import com.ifreeze.data.repo.auth.LocationHelper
import com.ifreeze.data.cash.PreferencesGateway
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


/**
 * A worker that performs automatic synchronization tasks, including network requests and system checks.
 *
 * This worker interacts with various APIs, checks system settings, and performs actions based on the results.
 * It uses coroutine-based work to ensure tasks are executed asynchronously.
 *
 * @param api An instance of [UserApi] for making network requests.
 * @param context The application context.
 * @param workerParameters The parameters for the worker.
 */
@HiltWorker
class AutoSyncWorker @AssistedInject constructor(
    @Assisted private val api: UserApi,
    @Assisted context: Context,
    @Assisted workerParameter: WorkerParameters,
) : CoroutineWorker(context, workerParameter), LocationHelper.LocationCallback {
    // Preference gateway for accessing saved preferences
    private val preference = PreferencesGateway(context)
    // Lists of allowed Wi-Fi networks and blocked websites retrieved from preferences
    private val allowedList = preference.getList("allowedWifiList")
    private val blockedWebsites = preference.getList("blockedWebsites")

    // Various intents for starting or stopping services
    private val deviceId = preference.load("responseID", "")
    private val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
    private val kioskIntent = Intent(context, ForceCloseKiosk::class.java)
    private val locationService = Intent(context, LocationService::class.java)
    private val installedAppsList = getInstalledApps(context)
    val hashesListDatabase = preference.getList("hashesListDatabase")

    // Variables for failure tracking and license ID
    private var failureCount = preference.load("failureCount", 0)
    private var isFailureLimitReached = preference.load("isFailureLimitReached", false)
    private val licenseID = preference.load("licenseID", "")

    // Settings and environment information
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )

    @SuppressLint("SimpleDateFormat")
    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    val checkItems = mutableListOf<Pair<String, Boolean>>()
    val lockedScreen = lockScreen(context)
    val rooted = deviceRootedEnable()
    val developerOptionsEnabled = developerOptionsEnabled(context)

    val downloadDirectory =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    companion object {
        // List of hashes for proactive scanning
        val hashesList = mutableListOf<Pair<String, String>>()
    }

    init {
        failureCount = preference.load("failureCount", 0)!!
        LocationHelper.getLocation(context, this)
    }


    /**
     * Called when location data is fetched.
     *
     * Performs the following tasks:
     * - Updates the base URL from the server if needed
     * - Checks and updates various settings and preferences
     * - Sends data to the server and processes responses
     * - Handles errors and updates failure count
     *
     * @param locationData The fetched location data.
     */
    override suspend fun onLocationFetched(locationData: LocationDataAddress) {
        try {
            // Retrieve the new base URL from the API
//            val baseUrl = "https://192.168.1.250/api/"
            val getNewBaseUrl = api.getCloudURL(deviceId!!)
            if (getNewBaseUrl.isSuccessful){
                val updatedUrl = getNewBaseUrl.body()?.data?.url
                Log.d("server", "updatedUrl from auto sync $updatedUrl")
                preference.saveBaseUrl(updatedUrl!!)
            }
//            preference.saveBaseUrl(baseUrl)

            // Check failure count and update preference
            if (failureCount!! >= 120) {
                isFailureLimitReached = true
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }
            else{
                isFailureLimitReached = false
                preference.save("isFailureLimitReached", isFailureLimitReached!!)
            }
            // Check necessary permissions and settings
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

            // Fetch location details
            val address = locationData.address ?: "Unknown Address"
            val latitude = locationData.latitude ?: 0.0
            val longitude = locationData.longitude ?: 0.0
            val mapUri = Uri.parse("https://maps.google.com/maps/search/$latitude,$longitude")

            val userLocation = LocationModel(
                location = mapUri.toString(),
                address = address,
                deviceId = deviceId!!
            )

            // Send user location data to the server
            val userLocationResponse = api.userLocation(userLocation)

            // Prepare and send mobile application data
            val mobileApplication = MobileApps(
                deviceId = deviceId,
                appName = installedAppsList
            )

            val checklicenseData = api.checkLicenseData(licenseID!!)
            if (checklicenseData.isSuccessful){
                val responseBody = checklicenseData.body()?.toString()
                val isLicenseValid = responseBody?.toBoolean() ?: false // Convert string to boolean
                preference.save("validLicense", isLicenseValid)
            }

            // Update mobile applications and handle responses
            val mobileApplications = api.mobileApps(mobileApplication)
            if (mobileApplications.isSuccessful) {
                failureCount = 0
                preference.save("failureCount", failureCount!!)
            }

            if (userLocationResponse.isSuccessful) {
            }


            val response = api.newUpdateUserData(deviceId)
            if (response.isSuccessful) {
                // Update preferences with new data from the server
                val applicationNamesList = response.body()?.data?.deviceKioskApps ?: emptyList()
                preference.saveList("kioskApplications", applicationNamesList)


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
                if (cloudBlockedWebSites != null) {
                preference.saveList("blockedWebsites", cloudBlockedWebSites)
                }
                val responseData = response.body()?.data?.device
                val blockedAppsList = response.body()?.data?.blockedApps
                if (blockedAppsList != null) { // Check for null
                    preference.saveList("blockedAppsList", blockedAppsList)
                }

                val allowedAppsList = response.body()?.data?.exceptionApps
                if (allowedAppsList != null) { // Check for null
                    preference.saveList("allowedAppsList", allowedAppsList)
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
                applicationContext.startService(serviceIntent)
            }
            // Check and update device status
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
            // Send alerts for any detected issues
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

                }
            }
            // Scan files for hashes and send proactive results if matched
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
                }
            }

            // Check for version updates and perform installation or uninstallation

            val num = preference.loadDouble("num", 0.9)!!
            val getVersionsDet = api.getAllVersionsDetails(num, deviceId)

            if (getVersionsDet.isSuccessful){
                val versionNumber= getVersionsDet.body()?.data?.map { it.versionNumber }
                val status = getVersionsDet.body()?.data?.map { it.status }

                val zipFilePath =  getVersionsDet.body()?.data?.map {   it.fileDownloadLink }
                val versionDescription = getVersionsDet.body()?.data?.map { it.versionDescription.substringBefore(".") }


                versionNumber?.forEachIndexed { index, version ->
                    val currentStatus = status?.get(index)
                    val currentZipFilePath = zipFilePath?.get(index)
                    val currentVersionDescription = versionDescription?.get(index)

                    if (version > num && currentStatus == "Install") {
                        downloadAndInstallZip(
                            "https://security.flothers.com:8443/Zip/Versions/InstalledApps/838d499d-8fcd-4357-948f-08dc07916c1e/1.0/Facebook.exe",
                            applicationContext,
                            currentVersionDescription!!
                        )
                        preference.saveDouble("num", version)
                    } else if (currentStatus == "Uninstall") {
                        uninstallPackage(applicationContext, currentVersionDescription!!)
                    }
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

/**
 * Retrieves a list of installed applications on the device.
 *
 * This function queries the package manager to get a list of applications that have
 * a launcher activity and returns their names.
 *
 * @param context The application context used to access the package manager.
 * @return A list of names of installed applications.
 */
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

/**
 * Checks if the device's screen lock is secure.
 *
 * This function uses the KeyguardManager to determine if the device has a secure lock screen.
 *
 * @param context The application context used to access the KeyguardManager.
 * @return True if the device has a secure lock screen, false otherwise.
 */
fun lockScreen(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}


/**
 * Checks if the device is rooted.
 *
 * This function attempts to execute a command that requires root access and checks
 * if it succeeds, indicating that the device is rooted.
 *
 * @return True if the device is rooted, false otherwise.
 */
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


/**
 * Checks if developer options are enabled on the device.
 *
 * This function queries the Settings.Secure provider to check if the developer options
 * are enabled.
 *
 * @param context The application context used to access the content resolver.
 * @return True if developer options are enabled, false otherwise.
 */
fun developerOptionsEnabled(context: Context): Boolean {
    return Settings.Secure.getInt(
        context.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0
}

/**
 * Computes the SHA-256 hash of files in the specified directory.
 *
 * This function recursively scans a directory and computes the SHA-256 hash for each file.
 * The computed hashes are logged and added to a global list for further processing.
 *
 * @param directory The directory to scan for files.
 */
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


/**
 * Recursively scans a directory to compute hashes for files.
 *
 * This function traverses the directory structure and computes the SHA-256 hash for each file,
 * adding the results to the provided list.
 *
 * @param directory The directory to scan.
 * @param hashes The list to which file hashes are added.
 */
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

/**
 * Computes the SHA-256 hash of a file.
 *
 * This function reads the file in chunks and updates the SHA-256 digest to compute the hash.
 * The resulting hash is returned as a hexadecimal string.
 *
 * @param file The file to hash.
 * @return The SHA-256 hash of the file as a hexadecimal string.
 */
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

/**
 * Downloads and installs an APK file from the specified URL.
 *
 * This function uses the DownloadManager to download the APK file and then attempts to
 * install it. The installation process is handled by registering a BroadcastReceiver
 * to listen for download completion events.
 *
 * @param url The URL from which to download the APK.
 * @param context The application context used to access the DownloadManager and start the installation.
 * @param currentName The name of the APK file to be downloaded.
 */
@SuppressLint("UnspecifiedRegisterReceiverFlag")
private suspend fun downloadAndInstallZip(url: String, context: Context, currentName: String) = withContext(Dispatchers.IO) {
    try {
        val downloadManager = context.getSystemService(AppCompatActivity.DOWNLOAD_SERVICE) as DownloadManager
        val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val apkFile = File(downloadDirectory, "$currentName.apk")

        // Delete the existing file if it exists
        if (apkFile.exists()) {
            apkFile.delete()
        }

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle("Downloading APK")
            setDescription("Please wait...")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "$currentName-111.apk")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        }
        val downloadId = downloadManager.enqueue(request)
        Log.d("download", "downloadId $downloadId")

        suspendCancellableCoroutine<Unit> { cont ->
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    Log.d("download", "id $id")
                    installApk(context, File(downloadDirectory, "$currentName-111.apk"))

                }
            }
            context.registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
            cont.invokeOnCancellation {
                context.unregisterReceiver(receiver)
            }
        }
    } catch (e: Exception) {
        Log.e("download", "Error downloading and installing APK: ${e.message}", e)
    }
}


fun unzipFile(zipFile: File, targetDirectory: File) {
    Log.d("download", "Unzipping ${zipFile.path} to ${targetDirectory.path}")

    if (!targetDirectory.exists()) {
        targetDirectory.mkdirs()
        Log.d("download", "Created target directory ${targetDirectory.path}")
    }

    if (!zipFile.exists()) {
        Log.e("download", "Zip file not found: ${zipFile.path}")
        return
    }

    try {
        ZipInputStream(FileInputStream(zipFile)).use { zipInputStream ->
            var zipEntry: ZipEntry?
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val newFile = File(targetDirectory, zipEntry!!.name)
                Log.d("download", "Extracting ${newFile.path}")

                if (zipEntry!!.isDirectory) {
                    newFile.mkdirs()
                    Log.d("download", "Created directory ${newFile.path}")
                } else {
                    newFile.parentFile?.mkdirs()

                    // Handle existing file scenario
                    if (newFile.exists()) {
                        Log.w("download", "File already exists: ${newFile.path}. Overwriting.")
                        newFile.delete() // Delete the existing file
                    }

                    FileOutputStream(newFile).use { outputStream ->
                        val buffer = ByteArray(1024)
                        var len: Int
                        while (zipInputStream.read(buffer).also { len = it } > 0) {
                            outputStream.write(buffer, 0, len)
                        }
                    }
                    Log.d("download", "Successfully extracted ${newFile.path}")
                }
            }
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        Log.e("download", "File not found: ${e.message}")
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("download", "IO Exception: ${e.message}")
    } catch (e: Exception) {
        e.printStackTrace()
        Log.e("download", "Exception: ${e.message}")
    }
}


/**
 * Installs an APK file using an Intent.
 *
 * This function initiates an APK installation by creating an Intent and launching it.
 * If the application does not have permission to install unknown apps, it prompts the user to grant this permission.
 *
 * @param context The application context used to start the installation activity.
 * @param apkFile The file object representing the APK to be installed.
 */
private fun installApk(context: Context?, apkFile: File) {
    if (apkFile.exists()) {
        val apkUri = FileProvider.getUriForFile(context!!, context.applicationContext.packageName + ".fileprovider", apkFile)
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        if (!context.packageManager.canRequestPackageInstalls()) {
            val intentInstall = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
                .setData(Uri.parse("package:${context.packageName}"))
            context.startActivity(intentInstall)

        } else {
            context.startActivity(intent)
        }
    } else {
        Log.e("download", "APK file does not exist: ${apkFile.path}")
    }
}

/**
 * Uninstalls an application by launching an uninstall intent.
 *
 * This function creates an Intent to remove the specified package from the device.
 *
 * @param context The application context used to start the uninstall activity.
 * @param packageName The package name of the application to be uninstalled.
 */
fun uninstallPackage(context: Context, packageName: String) {
    val intent = Intent(Intent.ACTION_DELETE)
    intent.data = Uri.parse("package:$packageName")
    context.startActivity(intent)
}