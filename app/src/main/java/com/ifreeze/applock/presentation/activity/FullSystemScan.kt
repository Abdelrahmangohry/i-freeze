package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
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

class FullSystemScan : AppCompatActivity() {

    // Define constants

    private val FILE_SIZE_ESTIMATE = 1024 // Estimated average file size in bytes
    var EXTERNAL_STORAGE_PERMISSION_CODE = 145
    // Views
    private lateinit var callLogTextView: TextView
    private lateinit var numberOfAffectedFiles: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var estimatedTimeTextView: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btn: Button

    //    private lateinit var btn2: Button
    private var hashesList = mutableListOf<Pair<String, String>>()

    private var affectedList = mutableListOf<String>()
    private lateinit var numberOfScannedFiles: TextView
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var preference: PreferencesGateway
    private lateinit var database: SQLiteDatabase


    // Variables
    private var totalSize: Long = 0
    private var processedSize: Long = 0
    private var startTime: Long = 0
    private var counter = 0

    @OptIn(DelicateCoroutinesApi::class)
    @SuppressLint("SetTextI18n", "MissingInflatedId", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_system_scan)
        preference = PreferencesGateway(this)
        val hashesListDatabase = preference.getList("hashesListDatabase")
        // Request permission if not granted

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

        GlobalScope.launch(Dispatchers.IO) {
            //Copy the database file from assets to internal storage
                Log.d("abdo", "hashlist $hashesList")
                // Compare the lists here and update UI accordingly
                for (pair in hashesList) {
                    if (pair.second in hashesListDatabase) {
                        affectedList.add(pair.first)
                        Log.d("abdo", "affectedList $affectedList")
                    }
                }
            }


        // Initialize loading spinner
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        numberOfAffectedFiles = findViewById(R.id.numberOfEffectedFiles)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // Initialize views
        callLogTextView = findViewById(R.id.callLogTextView)
        progressBar = findViewById(R.id.progressBar)
        estimatedTimeTextView = findViewById(R.id.estimatedTimeTextView)
        btn = findViewById(R.id.btn)
//        btn2 = findViewById(R.id.btn2)
        numberOfScannedFiles = findViewById(R.id.numberOfScannedFiles)

        // Button click listener
        btn.setOnClickListener {
            numberOfScannedFiles.text = ""
            progressBar.progress = 0 // Reset progress bar
            startTime = System.currentTimeMillis() // Start time for estimating time
            counter = 0
            val downloadDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            Log.d("abdo", "rootDirectory $downloadDirectory")
            GlobalScope.launch(Dispatchers.IO) {

                processedSize = 0 // Reset processed size
                progressBar.progress = 0 // Reset progress bar
                startTime = System.currentTimeMillis()

                runOnUiThread {
                    loadingProgressBar.visibility = View.VISIBLE
                    estimatedTimeTextView.visibility = View.GONE
                    numberOfAffectedFiles.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    callLogTextView.visibility = View.GONE
                }
                totalSize = getTotalSize(downloadDirectory)
                Log.d("abdo", "total size $totalSize")
                runOnUiThread {
                    loadingProgressBar.visibility = View.GONE
                    estimatedTimeTextView.visibility = View.VISIBLE
                    numberOfAffectedFiles.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    callLogTextView.visibility = View.VISIBLE
                }
                getHashCodeFromFiles(downloadDirectory)
                Log.d("abdo", "hashesList $hashesList")
                withContext(Dispatchers.Main) {
                    for (pair in hashesList) {
                        if (pair.second in hashesListDatabase) {
                            affectedList.add(pair.first)
                        }
                    }

                    recyclerView.adapter = AffectedFiles(affectedList)
                    numberOfAffectedFiles.text = "Number of infected files: ${affectedList.size}"
                    Log.d("abdo", "affectedlistsize = $affectedList")
                    numberOfScannedFiles.text = "Number of scanned files: $counter"
                }
            } // Start scanning

        }



    }


    // onRequestPermissionsResult function remains the same

    private fun getTotalSize(directory: File): Long {
        var totalSize = 0L
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                totalSize += getTotalSize(file)
            } else {
                totalSize += file.length()
            }
        }
        return totalSize
    }

    private suspend fun getHashCodeFromFiles(directory: File) {
        val rootDirectory = Environment.getExternalStorageDirectory()

        if (rootDirectory.exists() && rootDirectory.canRead()) {
            val hashes = mutableListOf<Pair<String, String>>()
            scanDirectory(directory, hashes)
            // Log each file path and its hash
            hashes.forEachIndexed { index, pair ->
                Log.d("HashLog", "File ${index + 1}: Path: ${pair.first}, Hash: ${pair.second}")
                hashesList.add(pair)
                counter++
            }
        } else {
            Log.d("HashLog", "Cannot access root directory.")
        }
    }


    private suspend fun scanDirectory(directory: File, hashes: MutableList<Pair<String, String>>) {
        Log.d("HashLog", "Scanning directory: ${directory.absolutePath}")

        if (directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    scanDirectory(file, hashes)
                } else {
                    val hash = getFileHash(file)
                    hashes.add(Pair(file.absolutePath, hash))
                    processedSize += file.length()
                    updateProgress()
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

    private suspend fun updateProgress() {
        withContext(Dispatchers.Main) {
            val progress = ((processedSize.toDouble() / totalSize) * 100).toInt()
            progressBar.progress = progress

            // Calculate estimated time left
            val currentTime = System.currentTimeMillis()
            val elapsedTime = currentTime - startTime
            val estimatedTimeLeft =
                ((elapsedTime.toDouble() / processedSize) * (totalSize - processedSize)).toLong()

            // Update TextViews
            callLogTextView.text = "$progress%"
            estimatedTimeTextView.text = formatTime(estimatedTimeLeft)
        }
    }

    private fun formatTime(millis: Long): String {
        val seconds = (millis / 1000) % 60
        val minutes = (millis / (1000 * 60)) % 60
        val hours = (millis / (1000 * 60 * 60)) % 24
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}