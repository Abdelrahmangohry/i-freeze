package com.lock.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lock.applock.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

class FullSystemScan : AppCompatActivity() {

    // Define constants
    private val EXTERNAL_STORAGE_PERMISSION_CODE = 101
    private val FILE_SIZE_ESTIMATE = 1024 // Estimated average file size in bytes

    // Views
    private lateinit var callLogTextView: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var estimatedTimeTextView: TextView
    private lateinit var btn: Button
    private lateinit var btn2: Button
    private var hashesList  = mutableListOf<String>()
    private lateinit var numberOfScannedFiles : TextView

    // Variables
    private var totalSize: Long = 0
    private var processedSize: Long = 0
    private var startTime: Long = 0
    private var counter = 0

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_system_scan)

        // Initialize views
        callLogTextView = findViewById(R.id.callLogTextView)
        progressBar = findViewById(R.id.progressBar)
        estimatedTimeTextView = findViewById(R.id.estimatedTimeTextView)
        btn = findViewById(R.id.btn)
        btn2 = findViewById(R.id.btn2)
        numberOfScannedFiles = findViewById(R.id.numberOfScannedFiles)

        // Button click listener
        btn.setOnClickListener {
            progressBar.progress = 0 // Reset progress bar
            startTime = System.currentTimeMillis() // Start time for estimating time
            GlobalScope.launch(Dispatchers.IO) {
                processedSize = 0 // Reset processed size
                progressBar.progress = 0 // Reset progress bar
                startTime = System.currentTimeMillis()
                val downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                Log.d("HashLog", "rootDirectory $downloadDirectory")
                totalSize = getTotalSize(downloadDirectory)
                Log.d("HashLog", "total size $totalSize")
                getHashCodeFromFiles(downloadDirectory)
                Log.d("HashLog", "hashesList $hashesList")
                withContext(Dispatchers.Main) {
                    numberOfScannedFiles.text = "Number of files Scanned: $counter"
                }
            } // Start scanning

        }


        btn2.setOnClickListener {
            progressBar.progress = 0 // Reset progress bar
            startTime = System.currentTimeMillis() // Start time for estimating time
            GlobalScope.launch(Dispatchers.IO) {
                processedSize = 0 // Reset processed size
                progressBar.progress = 0 // Reset progress bar
                startTime = System.currentTimeMillis()
                val rootDirectory = Environment.getExternalStorageDirectory()
                Log.d("HashLog", "rootDirectory $rootDirectory")
                totalSize = getTotalSize(rootDirectory)
                Log.d("HashLog", "total size $totalSize")
                getHashCodeFromFiles(rootDirectory)
                Log.d("HashLog", "hashesList $hashesList")
                withContext(Dispatchers.Main) {
                    numberOfScannedFiles.text = "Number of files Scanned: $counter"
                }
            } // Start scanning

        }

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
        Log.d("HashLog", "Root directory: $rootDirectory")

        if (rootDirectory.exists() && rootDirectory.canRead()) {
            val hashes = mutableListOf<Pair<String, String>>()
            scanDirectory(directory, hashes)
            // Log each file path and its hash
            hashes.forEachIndexed { index, pair ->
                Log.d("HashLog", "File ${index + 1}: Path: ${pair.first}, Hash: ${pair.second}")
                hashesList.add(pair.second)
                counter++
            }
        } else {
            Log.d("HashLog", "Cannot access root directory.")
        }
    }

    private fun countFiles(directory: File): Int {
        var count = 0
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                count += countFiles(file)
            } else {
                count++
            }
        }
        return count
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
        val digest = MessageDigest.getInstance("MD5")
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