package com.ifreeze.applock.presentation.activity

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.data.cash.PreferencesGateway



/**
 * Composable function that represents the WebManager screen. This screen provides
 * settings to manage blacklisted and whitelisted URLs, allowing the user to toggle
 * between these settings and navigate to respective screens for further management.
 *
 * @param navController The NavController used to manage navigation between different
 * screens in the app.
 */
@RequiresApi(34)
@Composable
fun WebManager(navController: NavController) {
    // Initialize PreferencesGateway to load and save preferences
    val preference = PreferencesGateway(LocalContext.current)

    // State for storing the current value of Blacklist toggle
    val isBlacklistedChecked = remember { mutableStateOf(preference.load("WebBlacklist", false)) }

    // State for storing the current value of Whitelist toggle
    val isWhitelistedChecked = remember { mutableStateOf(preference.load("WebWhitelist", false)) }

    // Main container for the WebManager screen
    Column(
        modifier = Modifier
            .fillMaxSize()// Fill the entire screen size
            .background(Color(0xFF175AA8))   // Set the background color
    ) {
        // Secondary container to hold the toggle settings
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp)
        ) {
            // Header with back button and screen title
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Web Filter")

            // ToggleSettingItem for Blacklisted URLs, allowing the user to enable/disable it
            isBlacklistedChecked.value?.let {

                ToggleSettingItem(
                    icon = R.drawable.web_off,  // Icon for the blacklist setting
                    mainText = "Blacklisted URLs", // Main text for the setting
                    subText = "The Blacklisted Websites", // Subtext for the setting
                    isChecked = it, // Current state of the blacklist toggle
                    onCheckedChange = { newValue ->
                        preference.save("WebBlacklist", newValue) // Save the new value in preferences
                        isBlacklistedChecked.value = newValue
                        if (isWhitelistedChecked.value!!) {
                            preference.update("WebWhitelist", !newValue) // Ensure the whitelist is updated accordingly
                            isWhitelistedChecked.value = !newValue
                        }

                    },

                    onClick = {
                        navController.navigate(Screen.BlackListWeb.route) // Navigate to the Blacklist management screen
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp)) // Spacer to add vertical spacing

            // ToggleSettingItem for Whitelisted URLs, allowing the user to enable/disable it
            ToggleSettingItem(
                icon = R.drawable.white_list,
                mainText = "Whitelisted URLs",
                subText = "The Whitelisted Websites",
                isChecked = isWhitelistedChecked.value!!,

                onCheckedChange = {
                    preference.save("WebWhitelist", it)
                    isWhitelistedChecked.value = it
                    if (isBlacklistedChecked.value!!) {
                        preference.update("WebBlacklist", !it)
                        isBlacklistedChecked.value = !it
                    }
                },
                onClick = {
                    navController.navigate(Screen.WhiteListWeb.route) // Navigate to the Whitelist management screen
                }
            )
        }
    }
}



