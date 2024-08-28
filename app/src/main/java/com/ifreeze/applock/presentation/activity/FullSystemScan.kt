package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.adapter.AffectedFiles
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest

/**
 * Activity class for performing a full system scan to detect affected files based on a hash list.
 * Manages permissions, initializes views, and performs the scanning operation.
 */
class FullSystemScan : AppCompatActivity() {

    var EXTERNAL_STORAGE_PERMISSION_CODE = 145
    // Views
    // Views
    private lateinit var callLogTextView: TextView // TextView for displaying the scan progress percentage
    private lateinit var numberOfAffectedFiles: TextView // TextView for displaying the number of affected files
    private lateinit var progressBar: ProgressBar // ProgressBar for showing scan progress
    private lateinit var estimatedTimeTextView: TextView // TextView for displaying estimated time left
    private lateinit var recyclerView: RecyclerView // RecyclerView for displaying the list of affected files
    private lateinit var btn: Button // Button to start the scan

    // Private variables
    private var hashesList = mutableListOf<Pair<String, String>>() // List to store file paths and their hashes
    private var affectedList = mutableListOf<String>() // List to store paths of affected files
    private lateinit var numberOfScannedFiles: TextView // TextView for displaying the number of scanned files
    private lateinit var loadingProgressBar: ProgressBar // ProgressBar for displaying loading status
    private lateinit var preference: PreferencesGateway // PreferencesGateway for managing preferences
    private lateinit var database: SQLiteDatabase // SQLiteDatabase for database operations

    // Variables for scan progress and timing
    private var totalSize: Long = 0 // Total size of files to be scanned
    private var processedSize: Long = 0 // Size of files that have been processed
    private var startTime: Long = 0 // Start time of the scan
    private var counter = 0 // Counter for the number of scanned files

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n", "MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_system_scan)

        // Initialize PreferencesGateway to manage preferences
        preference = PreferencesGateway(this)
        // Get the list of hashes from preferences
        val hashesListDatabase = preference.getList("hashesListDatabase")
        // Request necessary permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                    EXTERNAL_STORAGE_PERMISSION_CODE
                )
            }
        }else{
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    EXTERNAL_STORAGE_PERMISSION_CODE
                )
            }
        }

        // Launch coroutine to perform file scanning and comparison
        GlobalScope.launch(Dispatchers.IO) {
            //Copy the database file from assets to internal storage
                Log.d("abdo", "hashlist $hashesList")
            // Compare hashes from the list with the database and update affectedList
                for (pair in hashesList) {
                    if (pair.second in hashesListDatabase) {
                        affectedList.add(pair.first)
                        Log.d("abdo", "affectedList $affectedList")
                    }
                }
            }


        // Initialize UI elements
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        numberOfAffectedFiles = findViewById(R.id.numberOfEffectedFiles)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize views
        callLogTextView = findViewById(R.id.callLogTextView)
        progressBar = findViewById(R.id.progressBar)
        estimatedTimeTextView = findViewById(R.id.estimatedTimeTextView)
        btn = findViewById(R.id.btn)
        numberOfScannedFiles = findViewById(R.id.numberOfScannedFiles)

        // Set click listener for the scan button
        btn.setOnClickListener {
            btn.visibility = View.GONE
            numberOfScannedFiles.text = ""
            progressBar.progress = 0 // Reset progress bar
            startTime = System.currentTimeMillis() // Start time for estimating time
            counter = 0
            val downloadDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            // Launch coroutine to perform scanning
            GlobalScope.launch(Dispatchers.IO) {

                processedSize = 0 // Reset processed size
                progressBar.progress = 0 // Reset progress bar
                startTime = System.currentTimeMillis()

                // Update UI to show loading progress
                runOnUiThread {
                    loadingProgressBar.visibility = View.VISIBLE
                    estimatedTimeTextView.visibility = View.GONE
                    numberOfAffectedFiles.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    callLogTextView.visibility = View.GONE
                }
                totalSize = getTotalSize(downloadDirectory) // Calculate total size
                // Update UI to show scanning progress
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    estimatedTimeTextView.visibility = View.VISIBLE
                    numberOfAffectedFiles.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    callLogTextView.visibility = View.VISIBLE
                }
                // Start scanning files
                getHashCodeFromFiles(downloadDirectory)
                // Update UI with scan results
                withContext(Dispatchers.Main) {
                    for (pair in hashesList) {
                        if (pair.second in hashesListDatabase) {
                            affectedList.add(pair.first)
                        }
                    }

                    recyclerView.adapter = AffectedFiles(affectedList)
                    numberOfAffectedFiles.text = "Number of infected files: ${affectedList.size}"
                    numberOfScannedFiles.text = "Number of scanned files: $counter"
                }
            }

        }
    }

    /**
     * Recursively calculates the total size of files in the given directory.
     *
     * @param directory The directory to scan.
     * @return The total size of files in bytes.
     */
    private fun getTotalSize(directory: File): Long {
        var totalSize = 0L
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                totalSize += getTotalSize(file) // Recursively add sizes of subdirectories
            } else {
                totalSize += file.length() // Add size of individual file
            }
        }
        return totalSize
    }

    /**
     * Scans the given directory and computes hash codes for all files.
     *
     * @param directory The directory to scan.
     */
    private suspend fun getHashCodeFromFiles(directory: File) {
        val rootDirectory = Environment.getExternalStorageDirectory()

        if (rootDirectory.exists() && rootDirectory.canRead()) {
            val hashes = mutableListOf<Pair<String, String>>()
            scanDirectory(directory, hashes)
            // Log each file path and its hash
            hashes.forEachIndexed { index, pair ->
                hashesList.add(pair)
                counter++ // Increment the file counter
            }
        } else {
            Log.d("HashLog", "Cannot access root directory.")
        }
    }

    /**
     * Recursively scans a directory and computes hash codes for each file.
     *
     * @param directory The directory to scan.
     * @param hashes The list to store file paths and hash codes.
     */
    private suspend fun scanDirectory(directory: File, hashes: MutableList<Pair<String, String>>) {
        Log.d("HashLog", "Scanning directory: ${directory.absolutePath}")

        if (directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    scanDirectory(file, hashes) // Recursively scan subdirectories
                } else {
                    val hash = getFileHash(file) // Compute hash for the file
                    hashes.add(Pair(file.absolutePath, hash))
                    processedSize += file.length() // Update processed size
                    updateProgress() // Update progress bar and estimated time
                }
            }
        } else {
            Log.d("HashLog", "${directory.absolutePath} is not a directory.")
        }
    }

    /**
     * Computes the SHA-256 hash for the given file.
     *
     * @param file The file to hash.
     * @return The hash code as a hexadecimal string.
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
     * Updates the progress bar and estimated time left on the UI thread.
     */
    private suspend fun updateProgress() {
        withContext(Dispatchers.Main) {
            val progress = ((processedSize.toDouble() / totalSize) * 100).toInt()
            progressBar.progress = progress // Update progress bar

            // Calculate estimated time left
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime
            val estimatedTimeLeft =
                ((elapsedTime.toDouble() / processedSize) * (totalSize - processedSize)).toLong()

            // Update UI elements
            callLogTextView.text = "$progress%"
            estimatedTimeTextView.text = formatTime(estimatedTimeLeft)
        }
    }

    /**
     * Formats the given time in milliseconds to a string in HH:MM:SS format.
     *
     * @param millis Time in milliseconds.
     * @return Formatted time string.
     */
    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}