package com.ifreeze.applock.presentation.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.service.ForceCloseKiosk
import com.ifreeze.applock.service.LocationService
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway


@RequiresApi(34)
@Composable
fun SettingAdmin(navController: NavController) {
    // Main container for the settings screen with a background color.
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        // Header menu with a back button and title text.
        HeaderMenu(onBackPressed = { navController.popBackStack() }, "Settings")
        // Main content of the settings screen containing admin-related options.
        GeneralOptionsUIAdmin()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@RequiresApi(34)
@Composable
fun GeneralOptionsUIAdmin() {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)

    // State variables for managing location and application block settings.
    var text by remember { mutableStateOf(preference.loadBaseUrl() ?: "") }
    val isLocationEnabled = preference.load("locationBlocked", false)
    val locationBlockedState = remember { mutableStateOf(isLocationEnabled) }
    val isApplicationBlocked = preference.load("BlockState", false)
    val lockedApplicationState = remember { mutableStateOf(isApplicationBlocked) }

    // Intents for starting and stopping services.
    val serviceIntent = Intent(context, LocationService::class.java)
    val kioskIntent = Intent(context, ForceCloseKiosk::class.java)

    // Container for all the admin settings.
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        // Toggle item for enabling/disabling location tracking.
        toggleLocationAdminItem(
            icon = R.drawable.map,
            mainText = "Track Location",
            subText = "Click Here to Track The Location",
            isChecked = locationBlockedState.value!!,
            onCheckedChange = { isCheckedLocation ->
                preference.update("locationBlocked", isCheckedLocation)
                locationBlockedState.value = isCheckedLocation
                if (isCheckedLocation && !isLocationEnabled(context)) {
                    context.startService(serviceIntent)
                } else {
                    context.stopService(serviceIntent)
                }
            },
            onClick = {
                // Optional click action for the location item.
            }
        )

        // Toggle item for enabling/disabling kiosk mode.
        toggleLocationAdminItem(
            icon = R.drawable.lock,
            mainText = "Kiosk Mode",
            subText = "Click here to enable kiosk mode",
            isChecked = lockedApplicationState.value!!,
            onCheckedChange = { isApplicationBlocked ->
                Log.d("kiosk", "isApplicationBlocked $isApplicationBlocked")
                if (isApplicationBlocked) {
                    // Enable kiosk mode.
                    preference.update("BlockState", true)
                    lockedApplicationState.value = true
                    context.startService(kioskIntent)
                } else {
                    // Disable kiosk mode.
                    preference.update("BlockState", false)
                    lockedApplicationState.value = false
                    context.stopService(kioskIntent)
                }
            },
            onClick = {
                // Optional click action for the kiosk mode item.
            }
        )

        // Card containing a text field for managing server URL and a save button.
        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            onClick = { },
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                ) {
                    // Text field for entering the management server URL.
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Management Server", color = Color.Black) },
                        maxLines = 1,
                        textStyle = TextStyle(fontSize = 16.sp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Text color
                            unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                            focusedBorderColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Save button to save the entered server URL.
                    Button(
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
                        onClick = {
                            preference.saveBaseUrl(text)
                            Toast.makeText(context, "Base URL saved", Toast.LENGTH_LONG).show()
                        }
                    ) {
                        Text(text = "Save", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun toggleLocationAdminItem(
    icon: Int,
    mainText: String,
    subText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    // Card item with a toggle switch and text.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF175AA8))
            .padding(bottom = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Icon displayed on the card.
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = Shape.medium)
                        .background(Color(0xFF175AA8))
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                // Column containing text and switch.
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
                // Switch for toggling settings.
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                )
            }
        }
    }
}


