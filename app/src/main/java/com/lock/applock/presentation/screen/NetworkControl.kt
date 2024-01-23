package com.lock.applock.presentation.screen

import android.content.Intent
import android.util.Log
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lock.applock.R
import com.lock.applock.presentation.AuthViewModel
import com.lock.applock.presentation.activity.WhiteListItem
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.applock.service.NetworkMonitoringService
import com.lock.applock.ui.theme.Shape
import com.lock.data.model.DeviceInfo
import com.patient.data.cashe.PreferencesGateway

@Composable
fun NetworkControl(
    navController: NavController,
    wifi: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel()
) {

    val preference = PreferencesGateway(LocalContext.current)
    val serviceIntent = Intent(LocalContext.current, NetworkMonitoringService::class.java)
    val context = LocalContext.current

    val isWifiBlocked = preference.load("WifiBlocked", false)
    val wifiBlockedState = remember { mutableStateOf(isWifiBlocked) }

    val isWifiWhiteListing = preference.load("WifiWhite", false)
    val wifiAllowedState = remember { mutableStateOf(isWifiWhiteListing) }

    var wifiListAllowed = preference.getList("WifiList")
    var deviceID = "f9fb2f12-2276-4ff5-ae64-84f226cddb70"




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8)).padding(horizontal = 14.dp)
    ) {
        Column(

        ) {
            HeaderMenu(onBackPressed = { navController.popBackStack() })
        }

        wifiBlockedState.value?.let {
            ToggleSettingItem(
                icon = R.drawable.wifi_icon,
                mainText = "Block WiFi",
                subText = "Click Here to Block WiFi",
                isChecked = it,
                onCheckedChange = { isChecked ->

                    wifiBlockedState.value = it
                    preference.update("WifiBlocked", isChecked)
                    wifiBlockedState.value = isChecked
                    if (isChecked) {
                        Log.d("abdo", "i am here")
                        context.startService(serviceIntent)
//                        checkedPreferencesUpdates(authViewModel,deviceID)
//                        authViewModel.updateUserData(deviceID, deviceInfo)
                    } else {
                        Log.d("abdo", "iam in else")
                        context.stopService(serviceIntent)
//                        checkedPreferencesUpdates(authViewModel,deviceID)
                    }
                    if (wifiAllowedState.value!!) {
                        preference.update("WifiWhite", it)
                        wifiAllowedState.value = it
                    }
                },
                onClick = {

                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        // Custom composable for Whitelisted Apps (conditionally displayed)

        ToggleSettingItem(
            icon = R.drawable.white_list,
            mainText = "WhiteList WiFi",
            subText = "The White List WiFi",
            isChecked = wifiAllowedState.value!!,

            onCheckedChange = {
                preference.save("WifiWhite", it)
                wifiAllowedState.value = it

                if (wifiAllowedState.value!! && wifiListAllowed.isEmpty()) {
                    Log.d("abdo", "i am here")
                    context.startService(serviceIntent)
                } else {
                    Log.d("abdo", "iam in else")
                    context.stopService(serviceIntent)
                }

                if (wifiBlockedState.value!!) {
                    preference.update("WifiBlocked", !it)
                    wifiBlockedState.value = !it
                }


            },
            onClick = {
                navController.navigate(Screen.WhiteListWifi.route)
            }
        )

    }
}
@Composable
fun checkedPreferencesUpdates(authViewModel: AuthViewModel = hiltViewModel(), deviceID : String) {
    val preference = PreferencesGateway(LocalContext.current)
    val isWifiBlocked = preference.load("WifiBlocked", false)
    val isWifiWhiteListing = preference.load("WifiWhite", false)
    //var deviceID = "f9fb2f12-2276-4ff5-ae64-84f226cddb70"
    val deviceInfo =
        DeviceInfo(
            BlockWiFi= isWifiBlocked ?: false,
            WhiteListWiFi = isWifiWhiteListing ?: false,
            BlockListURLs = true,
            WhiteListURLs = false,
            BlockListApps = true,
            WhiteListApps = false,
            Browsers = true,
        )
//    authViewModel.updateUserData(deviceID, deviceInfo)

}


@Composable
fun ToggleSettingItem(
    icon: Int,
    mainText: String,
    subText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF175AA8))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),


                ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(shape = Shape.medium)
                            .background(Color(0xFF175AA8))
                    ) {
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

                    Switch(
                        checked = isChecked,
                        onCheckedChange = onCheckedChange,

                        )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleInputTextWithButton(onValidMacSubmit: (String) -> Unit) {
    val text = remember { mutableStateOf("") }
    val isValidMac = remember(text.value) { isMacAddress(text.value) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(15.dp),
            onValueChange = { newText ->
                text.value = newText
            },
            label = { Text(text = "MAC Address") },
            placeholder = { Text(text = "Enter MAC Address (XX:XX:XX:XX:XX:XX)") },
            isError = !isValidMac
        )
        if (!isValidMac) {
            Text(
                "Invalid MAC Address format",
                color = Color.Red,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Button(
            onClick = {
                if (isValidMac) {
                    onValidMacSubmit(text.value)
                    text.value = "" // Clear the text field
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            enabled = isValidMac // Enable the button only if the MAC address is valid
        ) {
            Text("Submit")
        }
    }
}

fun isMacAddress(mac: String): Boolean {
    val regex = Regex("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$")
    return mac.matches(regex)
}


@Composable
fun HeaderMenu(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Network Control",
            color = Color.White,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 30.dp).padding(horizontal = 55.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}