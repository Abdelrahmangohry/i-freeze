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
import com.patient.data.cashe.PreferencesGateway


@RequiresApi(34)
@Composable
fun WebManager(navController: NavController) {
    val preference = PreferencesGateway(LocalContext.current)
    val isBlacklistedChecked = remember { mutableStateOf(preference.load("WebBlacklist", false)) }
    val isWhitelistedChecked = remember { mutableStateOf(preference.load("WebWhitelist", false)) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp)
        ) {
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Web Filter")

            // Custom composable for Blacklisted Apps

            isBlacklistedChecked.value?.let {

                ToggleSettingItem(
                    icon = R.drawable.web_off,
                    mainText = "Blacklisted URLs",
                    subText = "The Blacklisted Websites",
                    isChecked = it,
                    onCheckedChange = {
                        preference.save("WebBlacklist", it)
                        isBlacklistedChecked.value = it
                        if (isWhitelistedChecked.value!!) {
                            preference.update("WebWhitelist", !it)
                            isWhitelistedChecked.value = !it
                        }

                    },

                    onClick = {
                        navController.navigate(Screen.BlackListWeb.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom composable for Whitelisted Apps (conditionally displayed)

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
                    navController.navigate(Screen.WhiteListWeb.route)
                }
            )

        }
    }
}



