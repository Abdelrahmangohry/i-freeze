package com.lock.applock.presentation.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lock.applock.R
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.applock.service.LocationService
import com.lock.applock.service.NetworkMonitoringService
import com.lock.applock.service.startAutoSyncWorker
import com.lock.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway

@Composable
fun AdminAccess(navController: NavController, webStart: () -> Unit, viewModel: AppsViewModel = hiltViewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        autoSyncButton()
        HeaderLogo()
        GeneralOptionsUI(navController, webStart)
    }
}

@Composable
fun autoSyncButton() {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")


    val locationService = Intent(context, LocationService::class.java)
    Row(modifier = Modifier.padding(15.dp)) {
        Button(
            onClick = {

                val locationPermissionRequestCode = 456
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if (!isOnline(context)) {
                    return@Button
                }
                if (isLocationPermissionGranted(context)) {
                    if (isLocationEnabled(context)) {
                        Log.d("abdo", "autoSync started")
                        Toast.makeText(context, "Data Synchronized Successfully", Toast.LENGTH_SHORT).show()
                        context.stopService(locationService)
                        startAutoSyncWorker(context)
                    }
                    else {
                        Log.d("abdo", "i must start service")
                        context.startService(locationService)
                        Toast.makeText(context, "Please Enable Location", Toast.LENGTH_SHORT).show()
//                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                } else {
                    Toast.makeText(context, "Give I-Freeze The Location Permission", Toast.LENGTH_SHORT).show()
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionRequestCode
                    )
                }

            },
            colors = ButtonDefaults.buttonColors(Color.White),
            modifier = Modifier.clip(CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = null,
                tint = Color(0xFF175AA8)
            )
        }
    }
}


@Composable
fun HeaderLogo() {
    val logoImage = painterResource(id = R.drawable.ifreezelogo22)
    val fontAlger = FontFamily(Font(R.font.arial, FontWeight.Bold))

    Column(
        modifier = Modifier.fillMaxWidth()

    ) {
        Box(modifier = Modifier.fillMaxWidth())
        {

            Image(

                painter = logoImage,
                contentDescription = "iFreeze Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )

            Text(
                text = "Freeze Your Risks",
                fontFamily = fontAlger,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 90.dp, start = 10.dp)
                    .align(Alignment.Center),
                fontSize = 19.sp

            )

        }

    }

}

@Composable
fun GeneralOptionsUI(navController: NavController, webStart: () -> Unit ) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {

        GeneralSettingItem(
            icon = R.drawable.scan,
            mainText = "System Scan",
            subText = "Keep Your Mobile Secure and Initiate a Scan",
            onClick = {

                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                }
                else{
                    navController.navigate(Screen.Scan.route)
                }


            }
        )

        GeneralSettingItem(
            icon = R.drawable.web,
            mainText = "Web Access",
            subText = "Roam within the Allowed Boundaries",
            onClick = {

                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                }
                else{
                    webStart()
                }

            }
        )

        GeneralSettingItem(
            icon = R.drawable.admin,
            mainText = "Admin Login",
            subText = "Administrative Privileges",
            onClick = {
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                }
                else{
                    navController.navigate(Screen.Login.route)
                }
            }
        )


        GeneralSettingItem(
            icon = R.drawable.icon_settings,
            mainText = "Settings",
            subText = "Configure App Permissions",
            onClick = {
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                }
                else{
                    navController.navigate(Screen.Setting.route)
                }


            }
        )

        GeneralSettingItem(
            icon = R.drawable.contact_support,
            mainText = "Support",
            subText = "Contact Us",
            onClick = {

                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                }
                else{
                      navController.navigate(Screen.SupportTeam.route)
                }
            }
        )

        GeneralSettingItem(
            icon = R.drawable.baseline_key_24,
            mainText = "License Activation",
            subText = "Activate Your License",
            onClick = {
                navController.navigate(Screen.LicenseActivation.route)

            }
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingItem(icon: Int, mainText: String, subText: String, onClick: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = { onClick() },
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()


    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 14.dp)
                    .fillMaxWidth(),


                ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

                    Spacer(modifier = Modifier.width(9.dp))
                    Column(
                        modifier = Modifier.offset(y = (2).dp)
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

                }


            }
        }
    }
}




