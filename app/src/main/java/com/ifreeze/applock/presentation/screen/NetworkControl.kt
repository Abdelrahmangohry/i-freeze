package com.ifreeze.applock.presentation.screen

import android.content.Intent
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.activity.HeaderMenu
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.service.NetworkMonitoringService
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway

/**
 * Composable function for the Network Control screen.
 *
 * This screen allows users to manage network settings, such as blocking Wi-Fi and defining
 * whitelisted Wi-Fi connections. It provides toggles to enable or disable these settings
 * and navigates to a different screen for whitelisted Wi-Fi configurations.
 *
 * @param navController The [NavController] used for navigating between screens.
 * @param wifi A lambda function to handle Wi-Fi-related actions.
 */
@Composable
fun NetworkControl(navController: NavController, wifi: () -> Unit) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
    val isWifiBlocked = preference.load("WifiBlocked", false)
    val wifiBlockedState = remember { mutableStateOf(isWifiBlocked) }
    val isWifiWhiteListing = preference.load("WifiWhite", false)
    val wifiAllowedState = remember { mutableStateOf(isWifiWhiteListing) }
    val wifiListAllowed = preference.getList("WifiList")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8)).padding(horizontal = 14.dp)
    ) {
        Column(

        ) {
            // Displays the header menu with a back button and title.
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Network Control")
        }
        // Displays a toggle item for blocking Wi-Fi connections.
        wifiBlockedState.value?.let {
            ToggleSettingItem(
                icon = R.drawable.wifi_icon,
                mainText = "Block Wi-Fi",
                subText = "Click here to Block Wi-Fi connections",
                isChecked = it,
                onCheckedChange = { isChecked ->
                    preference.update("WifiBlocked", isChecked)
                    wifiBlockedState.value = isChecked

                    if (isChecked) {
                        context.startService(serviceIntent)
                    } else {
                        context.stopService(serviceIntent)
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

        // Displays a toggle item for whitelisting Wi-Fi connections.
        ToggleSettingItem(
            icon = R.drawable.white_list,
            mainText = "WhiteList Wi-Fi",
            subText = "Define whitelisted Wi-Fi connections",
            isChecked = wifiAllowedState.value!!,

            onCheckedChange = {
                preference.save("WifiWhite", it)
                wifiAllowedState.value = it

                if (wifiAllowedState.value!! && wifiListAllowed.isEmpty()) {
                    context.startService(serviceIntent)
                } else {
                    context.stopService(serviceIntent)
                }

                if (wifiBlockedState.value!!) {
                    preference.update("WifiBlocked", !it)
                    wifiBlockedState.value = !it
                }
            },
            onClick = {
                // Navigate to the screen for managing whitelisted Wi-Fi connections.
                navController.navigate(Screen.WhiteListWifi.route)
            }
        )
    }
}


/**
 * Composable function for a toggle setting item.
 *
 * This component displays a setting item with an icon, main text, a subtext, and a switch
 * to toggle the setting on or off. It also provides a click listener for additional actions.
 *
 * @param icon The resource ID of the icon to display.
 * @param mainText The main text to display in the item.
 * @param subText The subtext to display in the item.
 * @param isChecked A Boolean indicating whether the switch is currently checked.
 * @param onCheckedChange A lambda function to handle changes in the switch state.
 * @param onClick A lambda function to handle click events on the item.
 */
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
        // Displays the icon for the setting.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
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
                // Displays the main text and subtext for the setting.
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
                // Displays the switch for toggling the setting.
                Switch(
                    checked = isChecked,
                    onCheckedChange = onCheckedChange,
                    )
            }
        }
    }
}