package com.ifreeze.applock.presentation.activity

import android.app.KeyguardManager
import android.content.Context
import android.provider.Settings
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ifreeze.applock.presentation.AuthViewModel
import com.ifreeze.data.model.AlertBody
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ScanProperties(navController: NavController, lifecycle: LifecycleOwner) {
    val context = LocalContext.current
    val authViewModel: AuthViewModel = hiltViewModel()
    val sharedPreferences = PreferencesGateway(context)
    // State variables for managing progress and loading state
    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    // Coroutine scope for handling asynchronous tasks
    val scope = rememberCoroutineScope()

    // Check device security and configuration status
    val lockedScreen = hasLockScreenPassword(context)
    val rooted = isDeviceRooted()
    val developerOptionsEnabled = areDeveloperOptionsEnabled(context)
    val untrustedAppsList by remember { mutableStateOf(sharedPreferences.getList(("UntrustedApps"))) }

    // Function to send an alert to the view model
    fun sendAlert(issueName: String) {
        // Format the current time
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        val message: List<AlertBody> = listOf(
            AlertBody(
                deviceId = "4E29E0D6-FE60-4AE9-B3CE-680B2F2C1A2F", // Example device ID
                logName = issueName, // Name of the issue
                time = currentTime, // Current timestamp
                action = "String", // Placeholder for action
                description = "String", // Placeholder for description
                source = "String" // Placeholder for source
            )
        )
        // Send the alert message using the view model
        authViewModel.sendAlert(message)
    }

    // Function to start a scan and update progress
    fun performScan() {
        clicked = false
        loading = true
        scope.launch {
            loadProgress { progress ->
                currentProgress = progress
            }
            loading = false
            clicked = true
        }
    }
    // Check network availability and fetch untrusted apps if available
    if (isNetworkAvailable(context)) {
        authViewModel.unTrustedApps()
        authViewModel._untrustedAppsFlow.observe(lifecycle, Observer { response ->
            if (response.isSuccessful) {
                val appNames: List<String> =
                    response.body()?.data?.map { it.appName } ?: emptyList()
                sharedPreferences.saveList("UntrustedApps", appNames)
            }
        })
    }
    // Main Column layout for scan properties screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8)),
    ) {
        // Back navigation button
        BackArrow(onBackPressed = { navController.popBackStack() })
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF175AA8)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Display scan results if the scan has been clicked
                if (clicked) {
                    ScanResults("Scan Results")
                    Column(
                        modifier = Modifier.fillMaxWidth().background(Color(0x33FFFFFF))
                            .padding(20.dp)

                    ) {
                        // List of check items with their status
                        val checkItems = mutableListOf<Pair<String, Boolean>>()
                        if (lockedScreen) {
                            CheckItem("Lock Screen", true)
                            checkItems.add("Lock Screen" to true)
                        } else {
                            CheckItem("There Is No Lock Screen", false)
                            checkItems.add("There Is No Lock Screen" to false)
                        }
                        CheckItem("Android Version", true)
                        checkItems.add("Android Version" to true)
                        CheckItem("Rooted Device", true)
                        checkItems.add("Rooted Device" to rooted)
                        if (developerOptionsEnabled) {
                            CheckItem("Developer options are enabled", false)
                            checkItems.add("Developer options are enabled" to false)
                        } else {
                            CheckItem("Developer Option Disabled", true)
                            checkItems.add("Developer Option Disabled" to true)
                        }
                        UntrustedApps("Untrusted Applications", untrustedAppsList)
                        // Send alerts for any issues found
                        checkItems.filter { it.second }.forEach { (issueName, _) ->
                            sendAlert(issueName)
                        }
                    }

                } else {
                    // Display scanning button and progress indicator
                    Box {
                        if (loading) {
                            CircularProgressBar(
                                percentage = currentProgress,
                                strokeWidth = 8.dp,
                                animationDuration = 500,
                                animationDelay = 0
                            )
                        }
                        Button(
                            onClick = { performScan() },
                            border = BorderStroke(width = 6.dp, color = Color.White),
                            modifier = Modifier
                                .size(300.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                        ) {
                            // Button text changes based on loading state
                            val buttonText = if (loading) {
                                "Scanning"
                            } else {
                                "Scan"
                            }
                            Text(
                                buttonText,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 50.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ScanResults(title: String) {
    // Column to display the scan results title
    Column(
        modifier = Modifier.fillMaxWidth(), // Takes up full width of the parent
        horizontalAlignment = Alignment.CenterHorizontally // Centers the text horizontally
    ) {
        Text(
            text = title, // Title text
            color = Color.White, // Text color
            fontWeight = FontWeight.Bold, // Bold font weight
            fontSize = 30.sp // Font size for the title
        )
    }
}

@Composable
fun UntrustedApps(title: String, appsList: List<String>) {
    // Row to display the title and status icon for untrusted apps
    Row(
        modifier = Modifier
            .fillMaxWidth() // Takes up full width of the parent
            .padding(vertical = 8.dp), // Vertical padding for the row
        horizontalArrangement = Arrangement.SpaceBetween // Distributes space between title and icon
    ) {
        Text(
            text = title, // Title text
            color = Color.White, // Text color
            fontWeight = FontWeight.Bold // Bold font weight
        )
        // Display an icon based on whether the app list is empty or not
        if (appsList.isEmpty()) {
            Icon(
                imageVector = Icons.Default.CheckCircle, // Check circle icon
                contentDescription = null, // No content description needed
                tint = Color.Green // Icon color
            )
        } else {
            Icon(
                imageVector = Icons.Default.Cancel, // Cancel icon
                contentDescription = null, // No content description needed
                tint = Color.Red // Icon color
            )
        }
    }

    // LazyColumn to display the list of untrusted apps
    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp) // Horizontal padding for the list
    ) {
        items(appsList.size) { index ->
            Text(text = appsList[index], color = Color.White) // Display each app name
        }
    }
}

@Composable
fun CheckItem(label: String, isChecked: Boolean) {
    // Row to display a check item with a label and status icon
    Row(
        modifier = Modifier
            .fillMaxWidth() // Takes up full width of the parent
            .padding(vertical = 4.dp), // Vertical padding for the row
        horizontalArrangement = Arrangement.SpaceBetween // Distributes space between label and icon
    ) {
        Text(text = label, color = Color.White) // Label text
        // Display a check or cancel icon based on the status
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null, // No content description needed
            tint = if (isChecked) Color.Green else Color.Red // Icon color based on status
        )
    }
}

// Function to simulate progress loading
suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100) // Update progress value
        delay(100) // Delay to simulate loading time
    }
}

@Composable
fun CircularProgressBar(
    percentage: Float,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    // State to control animation playback
    var animationPlayed by remember { mutableStateOf(false) }

    // Animate progress bar percentage
    var curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        ),
        label = ""
    )

    // Start animation when the composable is first launched
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    // Display the circular progress bar
    Column(
        modifier = Modifier.size(300.dp), // Size of the progress bar
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally // Center content within the column
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            drawArc(
                color = Color(0xFFee6c4d), // Arc color
                -90f, // Start angle
                360 * curPercentage.value, // Sweep angle based on progress
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round) // Stroke style
            )
        }
    }
}


// Helper function to check if the device has a lock screen password
@Composable
fun hasLockScreenPassword(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}

// Helper function to check if the device is rooted
@Composable
fun isDeviceRooted(): Boolean {
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

// Helper function to check if developer options are enabled
@Composable
fun areDeveloperOptionsEnabled(context: Context): Boolean {
    return Settings.Secure.getInt(
        context.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0
}



