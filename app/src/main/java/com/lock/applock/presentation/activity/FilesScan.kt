package com.lock.applock.presentation.activity

import android.annotation.SuppressLint
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest

//@Composable
//fun filesScan() {
//    val callLogText by remember { mutableStateOf("") }
//    var estimatedTimeText by remember { mutableStateOf("") }
//    var progress by remember { mutableIntStateOf(0) }
//    var counter by remember { mutableIntStateOf(0) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF175AA8)),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(text = callLogText)
//        LinearProgressIndicator(progress = progress / 100f)
//        Text(text = estimatedTimeText)
//        Button(onClick = { scanFiles(callLogText,  { estimatedTimeText = it }, { progress = it }, { counter = it }) }) {
//            Text(text = "Start Scanning")
//        }
//        Text(text = "Number of files Scanned: $counter")
//    }
//}
//
//@OptIn(DelicateCoroutinesApi::class)
//private fun scanFiles(
//    callLogText: String,
//    estimatedTimeText: (String) -> Unit,
//    progress: (Int) -> Unit, // Change to MutableState<Int>
//    counter:  (Int) -> Unit
//) {
//    GlobalScope.launch(Dispatchers.IO) {
//        var totalSize = 0L
//        var processedSize = 0L
//        var startTime = System.currentTimeMillis()
//        var hashesList = mutableListOf<String>()
//
//
//        val rootDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//        totalSize = getTotalSize(rootDirectory)
//        updateCallLogText("rootDirectory $rootDirectory \n total size $totalSize")
//
//        if (rootDirectory.exists() && rootDirectory.canRead()) {
//            val hashes = mutableListOf<Pair<String, String>>()
//            scanDirectory(rootDirectory, hashes) { size ->
//                processedSize = size
//                GlobalScope.launch(Dispatchers.IO) {
//                    updateProgress(processedSize, totalSize, startTime, progress, estimatedTimeText)
//                }
//                counter(hashes.size)
//            }
//            hashes.forEachIndexed { index, pair ->
//                updateCallLogText("\n File ${index + 1}: Path: ${pair.first}, Hash: ${pair.second}")
//                Log.d("abdo", "index: $index, path: ${pair.first}, hash: ${pair.second}")
//                hashesList.add(pair.second)
//            }
//        } else {
//            updateCallLogText("\n Cannot access root directory.")
//        }
//    }
//}
//
//private fun getTotalSize(directory: File): Long {
//    var totalSize = 0L
//    directory.listFiles()?.forEach { file ->
//        if (file.isDirectory) {
//            totalSize += getTotalSize(file)
//        } else {
//            totalSize += file.length()
//        }
//    }
//    return totalSize
//}
//
//private fun scanDirectory(
//    directory: File,
//    hashes: MutableList<Pair<String, String>>,
//    onProcessedSizeChange: (Long) -> Unit
//) {
//    if (directory.isDirectory) {
//        directory.listFiles()?.forEach { file ->
//            if (file.isDirectory) {
//                scanDirectory(file, hashes, onProcessedSizeChange)
//            } else {
//                val hash = getFileHash(file)
//                hashes.add(Pair(file.absolutePath, hash))
//                onProcessedSizeChange(file.length())
//            }
//        }
//    }
//}
//
//private fun getFileHash(file: File): String {
//    var digest = MessageDigest.getInstance("SHA-256")
//    var fis = FileInputStream(file)
//    var byteArray = ByteArray(1024)
//    var bytesCount: Int
//
//    while (fis.read(byteArray).also { bytesCount = it } != -1) {
//        digest.update(byteArray, 0, bytesCount)
//    }
//    fis.close()
//
//    val hashedBytes = digest.digest()
//
//    // Convert byte array to hexadecimal string
//    val stringBuilder = StringBuilder()
//    for (byte in hashedBytes) {
//        stringBuilder.append(String.format("%02x", byte))
//    }
//
//    return stringBuilder.toString()
//}
//
//@SuppressLint("SetTextI18n")
//private suspend fun updateProgress(
//    processedSize: Long,
//    totalSize: Long,
//    startTime: Long,
//    progress: (Int) -> Unit, // Change to MutableState<Int>
//    estimatedTimeText: (String) -> Unit
//) {
//    withContext(Dispatchers.Main) {
//        var progressValue = ((processedSize.toDouble() / totalSize) * 100).toInt()
//        progress(progressValue) // Use value property to set the new value
//
//        var currentTime = System.currentTimeMillis()
//        var elapsedTime = currentTime - startTime
//        var estimatedTimeLeft =
//            ((elapsedTime.toDouble() / processedSize) * (totalSize - processedSize)).toLong()
//
//        estimatedTimeText(formatTime(estimatedTimeLeft)) // Use value property to set the new value
//    }
//}
//
//private fun formatTime(millis: Long): String {
//    var seconds = (millis / 1000) % 60
//    var minutes = (millis / (1000 * 60)) % 60
//    var hours = (millis / (1000 * 60 * 60)) % 24
//    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
//}
//
//private fun updateCallLogText(text: String) {
//    // Update callLogText using state setter
//    // This should be done within a LaunchedEffect if this function is called from a background thread
//}

