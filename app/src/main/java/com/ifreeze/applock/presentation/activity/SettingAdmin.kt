package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ifreeze.applock.GeneralSettingItem
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.service.AdminService
import com.ifreeze.applock.service.ForceCloseKiosk

import com.ifreeze.applock.service.LocationService
import com.ifreeze.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway


@RequiresApi(34)
@Composable
fun SettingAdmin(navController: NavController, activity: Activity) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        HeaderAdmin(onBackPressed = { navController.popBackStack() })
        GeneralOptionsUIAdmin(activity)
    }
}

@Composable
fun HeaderAdmin(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Settings",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 30.dp).padding(horizontal = 75.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@RequiresApi(34)
@Composable
fun GeneralOptionsUIAdmin(
    activity: Activity,
) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceManager =
        activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(activity, MyDeviceAdminReceiver::class.java)
    val isAdminPermissionGranted = remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(preference.loadBaseUrl() ?: "") }

    val isLocationEnabled = preference.load("locationBlocked", false)
    val locationBlockedState = remember { mutableStateOf(isLocationEnabled) }

    val isApplicationBlocked = preference.load("BlockState", false)
    val lockedApplicationState = remember { mutableStateOf(isApplicationBlocked) }
    Log.d("kiosk", "lockedApplicationState $lockedApplicationState")

    val serviceIntent = Intent(context, LocationService::class.java)
    val kioskIntent = Intent(context, ForceCloseKiosk::class.java)
//    val lockedApplicationState = mutableStateOf(false)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            if (it) {
                Log.d("islam", "isAdminPermissionGranted : $it ")
                isAdminPermissionGranted.value = true
            }
        }
    )

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {

//        GeneralSettingItem(
//            icon = R.drawable.admin,
//            mainText = "Admin Permission",
//            subText = "For get access and control over apps",
//            onClick = {
//                if (!deviceManager.isDeviceOwnerApp(activity.packageName)) {
//                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
//                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
//                    intent.putExtra(
//                        DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                        "You should enable the app!"
//                    )
//                    activity.startActivityForResult(intent, 1)
//                } else {
//                    Toast.makeText(context, "the admin permission is add ", Toast.LENGTH_SHORT)
//                        .show()
//                }
//                Log.d("islam", "GeneralOptionsUISetting :admin ")
////                AdminAction()
//            }
//        )
//        GeneralSettingItem(
//            icon = R.drawable.draw,
//            mainText = "Over Draw ",
//            subText = "can create a layout over other apps",
//            onClick = {
//                Log.d("islam", "GeneralOptionsUISetting :drawAction ")
//
//                if (!Settings.canDrawOverlays(context)) {
//                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
//                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    context.startActivity(myIntent)
//                } else {
//                    Toast.makeText(
//                        context, "Over Draw Already Enabled", Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        )
//        GeneralSettingItem(
//            icon = R.drawable.ic_baseline_check_24,
//            mainText = "Accessibility Service",
//            subText = "This is essential part of Android's",
//            onClick = {
//                val enabledServicesSetting = Settings.Secure.getString(
//                    context.contentResolver,
//                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
//                )
//                if (enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true) {
//                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
//                    context.startActivity(intent)
//                } else {
//                    Toast.makeText(
//                        context,
//                        "Accessibility Service Already Enabled",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        )

//        GeneralSettingItem(
//            icon = R.drawable.location,
//            mainText = "Location Permission",
//            subText = "Enable location access for better functionality",
//            onClick = {
//                val locationPermissionRequestCode = 123
//                // Check if location permission is already granted
//                if (ContextCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // Location permission is not granted, request it
//                    ActivityCompat.requestPermissions(
//                        context as Activity,
//                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                        locationPermissionRequestCode
//                    )
//                } else {
//                    // Location permission is already granted
//                    Toast.makeText(
//                        context,
//                        "Location permission is already granted",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//            }
//        )

        toggleLocationAdminItem(
            icon = R.drawable.map,
            mainText = "Track Location",
            subText = "Click Here to Track The Location",
            isChecked = locationBlockedState.value!!,
            onCheckedChange = { isCheckedLocation ->

                preference.update("locationBlocked", isCheckedLocation)
                locationBlockedState.value = isCheckedLocation
                if (isCheckedLocation && !isLocationEnabled(context)) {
                    Log.d("abdo", "i am here")
                    Log.d("abdo", "isCheckedLocation $isCheckedLocation")
                    context.startService(serviceIntent)
                } else {
                    Log.d("abdo", "iam in else")
                    context.stopService(serviceIntent)
                }
            },
            onClick = {

            }
        )

        toggleLocationAdminItem(
            icon = R.drawable.lock,
            mainText = "Kiosk Mode",
            subText = "Click here to enable kiosk mode",
            isChecked = lockedApplicationState.value!!,
            onCheckedChange = { isApplicationBlocked ->
                Log.d("kiosk", "isApplicationBlocked $isApplicationBlocked")
                if (isApplicationBlocked) {
                    // Enable kiosk mode
                    preference.update("BlockState", true)
                    lockedApplicationState.value = true
                    context.startService(kioskIntent)
                    Log.d("abdo", "start lock")
                } else {
                    preference.update("BlockState", false)
                    lockedApplicationState.value = false
                    context.stopService(kioskIntent)
                    Log.d("abdo", "stop lock")
                }
            },
            onClick = {
                // Handle item click if needed
            }
        )

        ElevatedCard(
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            onClick = { },
            modifier = Modifier
                .padding(top = 15.dp)
                .fillMaxWidth()
        )
        {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(15.dp)
                ) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Management Server", color = Color.Black) },
                        maxLines = 1,
                        textStyle = TextStyle(fontSize = 16.sp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Text color // Color of the leading icon
                            unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                            focusedBorderColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Save Button
                    Button(
                        modifier = Modifier.padding(5.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
                        onClick = {
                            preference.saveBaseUrl(text)
                            Toast.makeText(context, "Base URL saved", Toast.LENGTH_LONG).show()
                        },

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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(Color(0xFF175AA8)).padding(bottom = 12.dp)
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


