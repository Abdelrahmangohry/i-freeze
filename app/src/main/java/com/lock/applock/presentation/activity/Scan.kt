package com.lock.applock.presentation.activity

import android.app.KeyguardManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.AuthViewModel
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader


@Composable
fun Scan(navController: NavController, lifecycle: LifecycleOwner) {
    val context = LocalContext.current
    val viewModel: AppsViewModel = hiltViewModel()
    val authViewModel: AuthViewModel = hiltViewModel()
    val sharedPreferences = PreferencesGateway(context)
    val newList = viewModel.articlesItems.collectAsState().value

    val appsNamesList = newList.map { it.appName }

    var currentProgress by remember { mutableStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    var clicked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val lockedScreen = hasLockScreenPassword(context)
    val rooted = isDeviceRooted()
    val developerOptionsEnabled = areDeveloperOptionsEnabled(context)
    val untrustedAppsList by remember { mutableStateOf(sharedPreferences.getList(("UntrustedApps"))) }

    if(isNetworkAvailable(context)){
        authViewModel.unTrustedApps()
        authViewModel._untrustedAppsFlow.observe(lifecycle, Observer { response ->
            if (response.isSuccessful) {
                val appNames: List<String> = response.body()?.data?.map { it.appName } ?: emptyList()
                sharedPreferences.saveList("UntrustedApps", appNames)
                Log.d("abdo", "this is Untrusted app $appNames")
            } else {
                Log.d("abdo", "this is Untrusted app error ${response.errorBody()}")

            }
        })
    }
    else{
        Toast.makeText(context, "Please Enable Internet Connection", Toast.LENGTH_SHORT).show()
    }


    val fakeAppsList = appsNamesList.filter { untrustedAppsList.contains(it) }
    Log.d("abdo", "fakeAppsList: $fakeAppsList")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8)),
    ) {
        backArrow(onBackPressed = { navController.popBackStack() })
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

                if (clicked) {
                    ScanResults("Scan Results")
                    Column(
                        modifier = Modifier.fillMaxWidth().background(Color(0x33FFFFFF))
                            .padding(20.dp)

                    ) {
                        if (lockedScreen) {
                            CheckItem("Lock Screen", true)
                        } else {
                            CheckItem("There Is No Lock Screen", false)
                        }
                        CheckItem("Android Version", true)
                        CheckItem("Rooted Device", true)
                        if (developerOptionsEnabled) {
                            CheckItem("Developer options are enabled", false)
                        } else {
                            CheckItem("Developer Option Disabled", true)
                        }
                        UntrustedApps("Untrusted Applications", fakeAppsList)
                    }

                } else {
                    Box {
                        if (loading) {
                            CircularProgressBar(
                                percentage = currentProgress,
                                number = 100,
                                fontSize = 28.sp,
                                radius = 50.dp,
                                color = Color.Green,
                                strokeWidth = 8.dp,
                                animationDuration = 500,
                                animationDelay = 0
                            )
                        }
                        Button(
                            onClick = {
                                clicked = false
                                loading = true
                                scope.launch {
                                    loadProgress { progress ->
                                        currentProgress = progress
                                    }
                                    loading = false
                                    clicked = true
                                }
                            },
                            border = BorderStroke(width = 6.dp, color = Color.White),
                            modifier = Modifier
                                .size(300.dp)
                                .clip(CircleShape),
                            colors = ButtonDefaults.buttonColors(Color.Transparent),
                        ) {
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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Composable
fun UntrustedApps(title: String, appsList: List<String>) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
        if (appsList.isEmpty()) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color.Green
            )
        } else {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }

    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        items(appsList.size) { index ->
            Text(text = appsList[index], color = Color.White)
        }
    }
}

@Composable
fun CheckItem(label: String, isChecked: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White)
        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            tint = if (isChecked) Color.Green else Color.Red
        )
    }
}

suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(100)
    }
}

@Composable
fun CircularProgressBar(
    percentage: Float,
    number: Int,
    fontSize: TextUnit = 28.sp,
    radius: Dp = 100.dp,
    color: Color = Color.Green,
    strokeWidth: Dp = 8.dp,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }
    var curPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay
        ), label = ""

    )
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }
    Column(
        modifier = Modifier.size(300.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Canvas(modifier = Modifier.size(300.dp)) {
            drawArc(
                color = Color(0xFFee6c4d),
                -90f,
                360 * curPercentage.value,
                useCenter = false,
                style = Stroke(strokeWidth.toPx(), cap = StrokeCap.Round)
            )

        }

//        Text(
//            text = (curPercentage.value * number).toInt().toString(),
//            color = Color.Black,
//            fontSize = fontSize,
//            fontWeight = FontWeight.Bold
//
//        )
    }
}

@Composable
fun hasLockScreenPassword(context: Context): Boolean {
    val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
    return keyguardManager.isKeyguardSecure
}

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

@Composable
fun areDeveloperOptionsEnabled(context: Context): Boolean {
    return Settings.Secure.getInt(
        context.contentResolver,
        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
        0
    ) != 0
}


@Composable
fun backArrow(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}

