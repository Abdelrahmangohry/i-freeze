package com.ifreeze.applock.presentation.screen

import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getListApps
import com.ifreeze.applock.presentation.AppsViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(navController: NavController , preferences: PreferencesGateway) {
    val isDisplayed = remember { (preferences.load("isDisplayed", false)) }

    val coroutineScope = rememberCoroutineScope()
    if (isDisplayed == true) {
        // Navigate to the home screen if terms are already accepted
        navController.navigate(Screen.AdminAccess.route)
    } else {
        // Display the terms and conditions screen
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8)),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Terms and Conditions",
                    color = Color.Black,
                    modifier = Modifier.padding(16.dp)
                )
                // Add actual terms and conditions text here

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                preferences.update("isDisplayed", true)
                                navController.navigate(Screen.AdminAccess.route)
                            }
                        }
                    ) {
                        Text(text = "Accept", color = Color.Black)
                    }
                    Button(
                        onClick = {
                            // Display "Thank you" message and exit the app
                            android.os.Process.killProcess(android.os.Process.myPid())
                        }
                    ) {
                        Text(text = "Refuse", color = Color.Black)
                    }
                }
            }
        }
    }
}
    // Splash screen content
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier.fillMaxSize()
//    ) {
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(
//                text = "Do you accept all terms and conditions?",
//                color = Color.Black,
//                modifier = Modifier.padding(16.dp)
//            )
//            Row(
//                horizontalArrangement = Arrangement.SpaceEvenly,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Button(
//                    onClick = {
//                        navController.navigate(Screen.Setting.route)
//                    }
//                ) {
//                    Text(text = "Accept", color = Color.Black)
//                }
//                Button(
//                    onClick = {
//                        // Display "Thank you" message and exit the app
//                        Log.d("SplashScreen", "Thank you")
//                        android.os.Process.killProcess(android.os.Process.myPid())
//                    }
//                ) {
//                    Text(text = "Refuse", color = Color.Black)
//                }
//            }
//        }
//    }
//}
