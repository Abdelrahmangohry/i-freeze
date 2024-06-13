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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.BuildConfig
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.AppsViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway


@RequiresApi(34)
@Composable
fun AppManager(navController: NavController ,viewModel: AppsViewModel = hiltViewModel()) {
    val preference = PreferencesGateway(LocalContext.current)
    val isBlacklistedChecked = remember { mutableStateOf(preference.load("Blacklist", false)) }
    val isWhitelistedChecked = remember { mutableStateOf(preference.load("Whitelist", false)) }
    val isBrowsersListedChecked = remember { mutableStateOf(preference.load("Browsers", false)) }

    if (BuildConfig.FLAVOR.equals("paid")) {
        // Code for the paid version
    } else {
        // Code for the free version
    }

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
            HeaderMenu(onBackPressed = { navController.popBackStack() })


            // Custom composable for Blacklisted Apps

            isBlacklistedChecked.value?.let {

                ToggleSettingItem(
                    icon = R.drawable.app_blocking,
                    mainText = "Blacklisted Apps",
                    subText = "Block certain mobile applications",
                    isChecked = it,
                    onCheckedChange = {
                        preference.save("Blacklist", it)
                        isBlacklistedChecked.value = it
                        if(isWhitelistedChecked.value!!){
                        preference.update("Whitelist", !it)
                        isWhitelistedChecked.value = !it
                        }

                    },

                    onClick = {
                 navController.navigate(Screen.BlackList.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Custom composable for Whitelisted Apps (conditionally displayed)

            ToggleSettingItem(
                icon = R.drawable.white_list,
                mainText = "Whitelisted Apps",
                subText = "Activate whitelisted applications",
                isChecked = isWhitelistedChecked.value!!,

                onCheckedChange = {
                    preference.save("Whitelist", it)
                    isWhitelistedChecked.value = it
                    if(isBlacklistedChecked.value!!){
                    preference.update("Blacklist", !it)
                    isBlacklistedChecked.value = !it
                    }
                },
                onClick = {
               navController.navigate(Screen.WhiteList.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Custom composable for Whitelisted Apps (conditionally displayed)

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

@Composable
fun ToggleSettingItem(
    icon: Int,
    mainText: String,
    subText: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: () -> Unit,

) {
    ElevatedCard(elevation = CardDefaults.cardElevation(
        defaultElevation = 8.dp
    ),
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
                        thumbContent = if (isChecked) {
                            {
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



@Composable
fun HeaderMenu(onBackPressed: () -> Unit) {
Row (modifier = Modifier.fillMaxWidth().padding(top = 20.dp)){
    IconButton(onClick = { onBackPressed() }) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = null,
            tint = Color.White
        )
    }
    Text(
        text = "App Manager",
        color = Color.White,

        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 30.dp).padding(horizontal = 35.dp),
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp
    )
}
}