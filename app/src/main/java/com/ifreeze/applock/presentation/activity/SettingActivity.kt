package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.service.LocationService
import com.ifreeze.applock.ui.theme.Shape
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
            text = "Permissions",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 30.dp).padding(horizontal = 75.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@RequiresApi(34)
@Composable
fun GeneralOptionsUISetting(
    activity:Activity,
) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(activity, MyDeviceAdminReceiver::class.java)
    val isAdminPermissionGranted = remember { mutableStateOf(false) }
    var text by remember { mutableStateOf(preference.loadBaseUrl() ?: "") }
    val isLocationEnabled = preference.load("locationBlocked", false)
    val locationBlockedState = remember { mutableStateOf(isLocationEnabled) }
    Log.d("abdo", "locationBlockedState ${locationBlockedState.value}")

    val serviceIntent = Intent(context, LocationService::class.java)

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
            subText = "Provide admin privilege to i-Freeze",
            onClick = {
                if (!deviceManager.isDeviceOwnerApp(activity.packageName)){
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!")
                    context.startActivity(intent)
                }else{
                    Toast.makeText(context,"the admin permission is add ", Toast.LENGTH_SHORT).show()
                }
            }
        )
        GeneralSettingItem(
            icon = R.drawable.draw,
            mainText = "Over Draw ",
            subText = "Enable the screen control option in settings",
            onClick = {
                Log.d("islam", "GeneralOptionsUISetting :drawAction ")

                if (!Settings.canDrawOverlays(context)) {
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(myIntent)
                }else{
                    Toast.makeText(
                        context,"Over Draw Already Enabled", Toast.LENGTH_SHORT).show()
                }
            }
        )

        GeneralSettingItem(
            icon = R.drawable.draw,
            mainText = "Install Unknown Apps",
            subText = "Enable the screen control option in settings",
            onClick = {
                Log.d("islam", "GeneralOptionsUISetting :drawAction ")


                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(settingsIntent)
                }
            }
        )

        GeneralSettingItem(
            icon = R.drawable.ic_baseline_check_24,
            mainText = "Accessibility Service",
            subText = "Activate the proactive feature in mobile settings",
            onClick = {
                val enabledServicesSetting = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                if (enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true){
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }else{
                    Toast.makeText(context,"Accessibility service is already enabled", Toast.LENGTH_SHORT).show()
                }
            }
        )

        GeneralSettingItem(
            icon = R.drawable.location,
            mainText = "Location Permission",
            subText = "Permit location accessibility",
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
                    Toast.makeText(context, "Location permission is already granted", Toast.LENGTH_SHORT).show()
                }
            }
        )

        GeneralSettingItem(

            icon = R.drawable.folder,
            mainText = "Files Permission",
            subText = "Enable i-Freeze to scan files",
            onClick = {
                val EXTERNAL_STORAGE_PERMISSION_CODEE = 1234
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            EXTERNAL_STORAGE_PERMISSION_CODEE
                        )
                    }else{
                        Toast.makeText(context, "Files access was granted", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            EXTERNAL_STORAGE_PERMISSION_CODEE
                        )
                    }


                }

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


