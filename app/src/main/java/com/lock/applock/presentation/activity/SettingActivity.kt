package com.lock.applock.presentation.activity

import SecondaryColor
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import android.util.Log
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.lock.applock.GeneralSettingItem
import com.lock.applock.R
import com.lock.applock.SupportItem
import com.lock.applock.service.AdminService
import com.lock.applock.service.ForceCloseWifi
import com.lock.applock.service.LocationService
import com.lock.applock.service.NetworkMonitoringService
import com.lock.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway


@RequiresApi(34)
@Composable
fun SettingScreen(navController: NavController, activity:Activity) {
    Column (
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ){
        HeaderSetting(onBackPressed = { navController.popBackStack() })
        GeneralOptionsUISetting(activity)
    }
}

@Composable
fun HeaderSetting(onBackPressed: () -> Unit) {
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
                .padding(top = 8.dp, bottom = 30.dp).padding(horizontal = 95.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}


@SuppressLint("SuspiciousIndentation")
@RequiresApi(34)
@Composable
fun GeneralOptionsUISetting(
    activity:Activity,
) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(activity, AdminService::class.java)
    val isAdminPermissionGranted = remember { mutableStateOf(false) }

    val isLocationEnabled = preference.load("locationBlocked", false)
    val locationBlockedState = remember { mutableStateOf(isLocationEnabled) }
    Log.d("abdo", "locationBlockedState ${locationBlockedState.value}")
    val serviceIntent   = Intent(context, LocationService::class.java)

    val launcher =  rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult ={
            if (it){
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

        GeneralSettingItem(
            icon = R.drawable.admin,
            mainText = "Admin Permission",
            subText = "For get access and control over apps",
            onClick = {
                if (!deviceManager.isAdminActive(compName)){
                    val adminIntent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                    adminIntent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin")
                    activity.startActivityForResult(adminIntent, 1)
                }else{
                    Toast.makeText(context,"the admin permission is add ", Toast.LENGTH_LONG).show()
                }
                Log.d("islam", "GeneralOptionsUISetting :admin ")
//                AdminAction()
            }
        )
        GeneralSettingItem(
            icon = R.drawable.draw,
            mainText = "Over Draw ",
            subText = "can create a layout over other apps",
            onClick = {
                Log.d("islam", "GeneralOptionsUISetting :drawAction ")

                if (!Settings.canDrawOverlays(context)) {
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(myIntent)
                }else{
                    Toast.makeText(
                        context,"its already here ", Toast.LENGTH_LONG).show()
                }
            }
        )
        GeneralSettingItem(
            icon = R.drawable.ic_baseline_check_24,
            mainText = "Accessibility Service",
            subText = "This is essential part of Android's",
            onClick = {
                Log.d("islam", "GeneralOptionsUISetting :accessibility ")
                val enabledServicesSetting = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (enabledServicesSetting?.contains("com.lock.applock.service.AccessibilityServices") != true){
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }else{
                    Toast.makeText(context,"its already isAccessibilityServiceEnabled ", Toast.LENGTH_LONG).show()
                }
            }
        )

        GeneralSettingItem(
            icon = R.drawable.location,
            mainText = "Location Permission",
            subText = "Enable location access for better functionality",
            onClick = {
                val locationPermissionRequestCode = 123
                // Check if location permission is already granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Location permission is not granted, request it
                    ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionRequestCode
                    )
                } else {
                    // Location permission is already granted
                    Toast.makeText(context, "Location permission is already granted", Toast.LENGTH_LONG).show()
                }
            }
        )

            toggleLocationSettingItem(
                icon = R.drawable.locationoff,
                mainText = "Block Location",
                subText = "Click Here to Block Location",
                isChecked = locationBlockedState.value!!,
                onCheckedChange = { isCheckedLocation ->

                    preference.update("locationBlocked", isCheckedLocation)
                    locationBlockedState.value = isCheckedLocation
                    if (isCheckedLocation && !isLocationEnabled(context)) {
                        Log.d("abdo", "i am here")
                        context.startService(serviceIntent)
                    } else {
                        Log.d("abdo", "iam in else")
                        context.stopService(serviceIntent)
                    }
                },
                onClick = {

                }
            )
    }
}


@Composable
fun toggleLocationSettingItem(
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


