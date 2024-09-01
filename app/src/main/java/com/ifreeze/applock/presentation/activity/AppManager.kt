package com.ifreeze.applock.presentation.activity

import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ifreeze.applock.BuildConfig
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway


/**
 * Composable function to manage app settings.
 * Includes toggles for Blacklist, Whitelist, and Browser block settings.
 *
 * @param navController Controls navigation between screens.
 */
@RequiresApi(34)
@Composable
fun AppManager(navController: NavController) {
    // Initialize preferences gateway to load and save settings.
    val preference = PreferencesGateway(LocalContext.current)
    // State to hold the value of Blacklist toggle (loaded from preferences).
    val isBlacklistedChecked = remember { mutableStateOf(preference.load("Blacklist", false)) }
    // State to hold the value of Whitelist toggle (loaded from preferences).
    val isWhitelistedChecked = remember { mutableStateOf(preference.load("Whitelist", false)) }
    // State to hold the value of Browsers block toggle (loaded from preferences).
    val isBrowsersListedChecked = remember { mutableStateOf(preference.load("Browsers", false)) }

    // Conditional logic for paid and free versions of the app.
    if (BuildConfig.FLAVOR.equals("paid")) {
        // Code for the paid version
    } else {
        // Code for the free version
    }
// Main container for the settings UI, with background color applied.
    Column(
        modifier = Modifier
            .fillMaxSize() // Fill the entire available space.
            .background(Color(0xFF175AA8))
    ) {
        // Nested column to arrange settings items vertically.
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp)
        ) {
            // Display the header menu with a back button.
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "App Manager")


            // Custom composable for Blacklisted Apps

            isBlacklistedChecked.value?.let {

                ToggleSettingItem(
                    icon = R.drawable.app_blocking,
                    mainText = "Blacklisted Apps",
                    subText = "Block certain mobile applications",
                    isChecked = it, // Bind the state to the toggle switch.
                    onCheckedChange = {isChecked ->
                        preference.save("Blacklist", isChecked)  // Save the state to preferences.
                        isBlacklistedChecked.value = isChecked // Update state.
                        // Automatically toggle Whitelist if Blacklist is enabled.
                        if(isWhitelistedChecked.value!!){
                        preference.update("Whitelist", !isChecked)
                        isWhitelistedChecked.value = !isChecked
                        }

                    },

                    onClick = {
                        // Navigate to the BlackList screen when clicked.
                 navController.navigate(Screen.BlackList.route)
                    }
                )
            }
            // Spacer to add vertical space between items.
            Spacer(modifier = Modifier.height(16.dp))

// Display the toggle for Whitelisted Apps.
            ToggleSettingItem(
                icon = R.drawable.white_list,
                mainText = "Whitelisted Apps",
                subText = "Activate whitelisted applications",
                isChecked = isWhitelistedChecked.value!!, // Bind the state to the toggle switch.

                onCheckedChange = {isChecked ->
                    preference.save("Whitelist", isChecked) // Save the state to preferences.
                    isWhitelistedChecked.value = isChecked // Update state.
                    // Automatically toggle Blacklist if Whitelist is enabled.
                    if(isBlacklistedChecked.value!!){
                    preference.update("Blacklist", !isChecked)
                    isBlacklistedChecked.value = !isChecked
                    }
                },
                onClick = {
                    // Navigate to the WhiteList screen when clicked.
               navController.navigate(Screen.WhiteList.route)
                }
            )
            // Spacer to add vertical space between items.
            Spacer(modifier = Modifier.height(16.dp))

// Display the toggle for Block Browsers.
            ToggleSettingItem(
                icon = R.drawable.browsers_12,
                mainText = "Block browsers",
                subText = "Disable all mobile browsers",
                isChecked = isBrowsersListedChecked.value!!,
                onCheckedChange = {
                    preference.save("Browsers", it)
                    isBrowsersListedChecked.value=it
                },
                onClick = {

                }
            )

        }
    }
}


/**
 * Composable function to display a setting item with a toggle switch.
 *
 * @param icon The resource ID for the icon to display.
 * @param mainText The main label text for the setting item.
 * @param subText The secondary explanatory text for the setting item.
 * @param isChecked The current state of the toggle switch.
 * @param onCheckedChange Lambda function called when the switch is toggled.
 * @param onClick Lambda function called when the item is clicked.
 */
@Composable
fun ToggleSettingItem(
    icon: Int,
    mainText: String,
    subText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,

) {
    // Create an elevated card with shadow, clickable to trigger onClick.
    ElevatedCard(elevation = CardDefaults.cardElevation(
        defaultElevation = 8.dp
    ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF175AA8))
    ) {
        Box(
            // Box container to arrange inner components.
            modifier = Modifier
                .fillMaxWidth() // Fill the entire width.
                .background(Color.White) // Set background color to white.
        ) {
            // Row to arrange icon and text vertically.
            Row(
                // Row to arrange the icon, text, and switch horizontally.
                modifier = Modifier.fillMaxWidth(),


                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Box to hold the icon with a background color.
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(shape = Shape.medium)
                            .background(Color(0xFF175AA8))
                    ) {
                        // Icon composable to display the provided icon resource.
                        Icon(
                            painter = painterResource(id = icon),
                            contentDescription = "",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    // Text and Switch
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = mainText,
                            color = Color(0xFF175AA8),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = subText,
                            color = Color(0xFF175AA8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,

                            )
                    }
                    // Toggle switch for enabling/disabling the setting.
                    Switch(
                        checked = isChecked, // Bind the switch state.
                        onCheckedChange = onCheckedChange, // Handle switch toggle event.
                        thumbContent = if (isChecked) {
                            {
                                // Icon inside the switch when checked.
                                Icon(
                                    imageVector = Icons.Filled.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(SwitchDefaults.IconSize),
                                )
                            }
                            }else {
                                null
                            }
                    )
                }
            }
        }
    }
}



/**
 * Composable function to display the header menu with a back button.
 *
 * @param onBackPressed Lambda function called when the back button is pressed.
 */
@Composable
fun HeaderMenu(onBackPressed: () -> Unit, text:String) {
    // Row to arrange back button and title horizontally.

    Row (modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        verticalAlignment = Alignment.CenterVertically
        ){
        // Icon button for back navigation.
    IconButton(onClick = { onBackPressed() }) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White
        )
    }
        // Title text for the header.
        Text(
            text = text,
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(end = 25.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}