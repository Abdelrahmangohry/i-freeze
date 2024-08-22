package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.AppsViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.service.LocationService
import com.ifreeze.applock.service.startAutoSyncWorker
import com.ifreeze.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway

@Composable
fun AdminAccess(
    navController: NavController,
    webStart: () -> Unit,
    screenShareFun: () -> Unit,
    viewModel: AppsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val locationService = Intent(context, LocationService::class.java)

    val EXTERNAL_STORAGE_PERMISSION_CODE = 1234
    when (PackageManager.PERMISSION_GRANTED) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) -> {
            Log.d("abdo", "autoSync started")
            if (!isLocationEnabled(context)) {
                context.startService(locationService)
            } else {
                context.stopService(locationService)
                startAutoSyncWorker(context)
            }
        }

        else -> {
//            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as Activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            EXTERNAL_STORAGE_PERMISSION_CODE
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically

        ) {
            autoSyncButton()
            dropDownOptions(navController)
        }
        HeaderLogo()
        GeneralOptionsUI(navController, webStart, screenShareFun)
    }
}

@SuppressLint("SuspiciousIndentation")
@Composable
fun dropDownOptions(navController: NavController) {

    var expanded by remember { mutableStateOf(false) }
    val handler = LocalUriHandler.current
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")
    var isFailureLimitReached = preference.load("isFailureLimitReached", false)
    var isLicenseValid = preference.load("validLicense", true)

    IconButton(
        onClick = { expanded = !expanded },

        ) {
        Icon(
            Icons.Default.MoreVert,
            contentDescription = "Options",
            tint = Color.White,
            modifier = Modifier.size(30.dp)
        )

        DropdownMenu(

            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Permissions") },
                onClick = {
                    navController.navigate(Screen.Setting.route)
                }
            )
            DropdownMenuItem(
                text = { Text("Help") },
                onClick = {
                    handler.openUri("https://ifreeze.flothers.com/expert/")
                }
            )
            DropdownMenuItem(
                text = { Text("License Activation") },
                onClick = {
                    navController.navigate(Screen.LicenseActivation.route)
                }
            )

            DropdownMenuItem(
                text = { Text("Start Kiosk") },
                onClick = {

                    if (preference.load("BlockState", true)!!) {
                        expanded = false
                        Toast.makeText(context, "Kiosk already activated", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        preference.update("BlockState", true)
                        expanded = false
                        Toast.makeText(context, "Kiosk mode activated", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }


}

@Composable
fun autoSyncButton() {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    val EXTERNAL_STORAGE_PERMISSION_CODE = 1235
    Row(modifier = Modifier.padding(15.dp)) {
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            EXTERNAL_STORAGE_PERMISSION_CODE
                        )
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            EXTERNAL_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                val locationPermissionRequestCode = 456
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }
                if (!isNetworkAvailable(context)) {
                    Toast.makeText(
                        context,
                        "You Should Enable Internet Connection",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@Button
                }

                if (
                    !Settings.canDrawOverlays(context) ||
                    enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Please enable i-Freeze permissions in app permissions",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@Button
                }

                if (isLocationPermissionGranted(context)) {
                    if (isLocationEnabled(context)) {
                        Log.d("abdo", "autoSync started")
                        Toast.makeText(
                            context,
                            "Data Synchronized Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        startAutoSyncWorker(context)
                    } else {
                        Log.d("abdo", "i must start service")
                        Toast.makeText(context, "Please Enable Location", Toast.LENGTH_SHORT).show()
//                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }

                } else {
                    Toast.makeText(
                        context,
                        "Give I-Freeze The Location Permission",
                        Toast.LENGTH_SHORT
                    ).show()
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
fun GeneralOptionsUI(navController: NavController, webStart: () -> Unit, screenShareFun: () -> Unit) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")
    var isFailureLimitReached = preference.load("isFailureLimitReached", false)
    var isLicenseValid = preference.load("validLicense", true)
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {

        GeneralSettingItem(
            icon = R.drawable.scan,
            mainText = "System Scan",
            subText = "Initiate a scan to detect mobile threats",
            onClick = {
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                } else if (
                    !Settings.canDrawOverlays(context) ||
                    enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Please enable i-Freeze permissions in app permissions",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@GeneralSettingItem

                } else if (isFailureLimitReached!! || !isLicenseValid!!) {
                    Toast.makeText(
                        context,
                        "License Failed Please connect to the management server",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@GeneralSettingItem
                } else {
                    navController.navigate(Screen.Scan.route)
                }


            }
        )

        GeneralSettingItem(
            icon = R.drawable.web,
            mainText = "Web Browser",
            subText = "Access websites safely with AI protection",
            onClick = {

                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                } else if (isFailureLimitReached!! || !isLicenseValid!!) {
                    Toast.makeText(
                        context,
                        "License Failed Please connect to the management server",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@GeneralSettingItem
                } else {
                    webStart()
                }

            }
        )

        GeneralSettingItem(
            icon = R.drawable.admin,
            mainText = "Admin Login",
            subText = "Admin portal login to change mobile policies",
            onClick = {
                Log.d("abdo", "isFailureLimitReached $isFailureLimitReached")
                Log.d("abdo", "isLicenseValid $isLicenseValid")
                if (deviceId.isNullOrEmpty()) {
                    Toast.makeText(context, "You Should Activate License", Toast.LENGTH_SHORT)
                        .show()
                    return@GeneralSettingItem
                } else if (isFailureLimitReached!! || !isLicenseValid!!) {
                    Toast.makeText(
                        context,
                        "License Failed Please connect to the management server",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@GeneralSettingItem
                } else if (
                    !Settings.canDrawOverlays(context) ||
                    enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(
                        context,
                        "Please enable i-Freeze permissions in app permissions",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    return@GeneralSettingItem

                } else {
                    navController.navigate(Screen.Login.route)
                }
            }
        )


        GeneralSettingItem(
            icon = R.drawable.contact_support,
            mainText = "Request Support",
            subText = "Create a new ticket with technical support",
            onClick = {
                navController.navigate(Screen.SupportTeam.route)
            }
        )

        GeneralSettingItem(
            icon = R.drawable.apps,
            mainText = "Kiosk Apps",
            subText = "Check permitted applications in Kiosk mode",
            onClick = {
                navController.navigate(Screen.KioskMode.route)
            }
        )

        GeneralSettingItem(
            icon = R.drawable.apps,
            mainText = "Screen Sharing",
            subText = "Check permitted applications in Kiosk mode",
            onClick = {
                screenShareFun()
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




