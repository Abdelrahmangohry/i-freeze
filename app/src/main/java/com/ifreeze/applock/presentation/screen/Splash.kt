package com.ifreeze.applock.presentation.screen

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.data.cash.PreferencesGateway
import kotlinx.coroutines.launch

/**
 * Composable function that displays the splash screen for the application.
 *
 * This screen shows the terms and conditions to the user. Based on whether the terms have
 * been previously accepted or not, the user will be navigated to the appropriate screen.
 *
 * @param navController The [NavController] used for navigation between screens.
 * @param preferences The [PreferencesGateway] used to check and update the preference indicating
 *                    whether the terms and conditions have been displayed.
 */
@Composable
fun SplashScreen(navController: NavController , preferences: PreferencesGateway) {
    // Remember the state indicating if the terms and conditions have been previously accepted
    val isDisplayed = remember { (preferences.load("isDisplayed", false)) }
    // Coroutine scope for handling asynchronous tasks
    val coroutineScope = rememberCoroutineScope()
    if (isDisplayed == true) {
        // Navigate to the Admin Access screen if terms are already accepted
        navController.navigate(Screen.AdminAccess.route)
    } else {
        // Display the terms and conditions screen
        Box(
            // Display the terms and conditions screen
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
                // Placeholder for actual terms and conditions text
                // Add terms and conditions content here
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Update the preference to indicate terms have been accepted
                                preferences.update("isDisplayed", true)
                                // Navigate to the Admin Access screen
                                navController.navigate(Screen.AdminAccess.route)
                            }
                        }
                    ) {
                        Text(text = "Accept", color = Color.Black)
                    }
                    Button(
                        onClick = {
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