package com.ifreeze.applock.presentation.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver


@RequiresApi(34)
@Composable
fun SettingScreen(navController: NavController, activity: Activity) {
    // Main column layout for the settings screen
    Column(
        modifier = Modifier
            .fillMaxSize() // Fills the maximum size of the parent
            .background(Color(0xFF175AA8)) // Background color of the column
    ) {
        // Header menu with a back navigation button and title
        HeaderMenu(onBackPressed = { navController.popBackStack() }, "Permissions")

        // General settings UI for managing permissions and settings
        GeneralOptionsUISetting(activity)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SuspiciousIndentation")
@RequiresApi(34)
@Composable
fun GeneralOptionsUISetting(
    activity:Activity,
) {
    // Obtain context and device manager for managing device policies
    val context = LocalContext.current
    val deviceManager = activity.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(activity, MyDeviceAdminReceiver::class.java)
    // Column layout for general settings items
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        // Admin permission setting item
        GeneralSettingItem(
            icon = R.drawable.admin,
            mainText = "Admin Permission",
            subText = "Provide admin privilege to i-Freeze",
            onClick = {
                // Check if the app is not a device owner
                if (!deviceManager.isDeviceOwnerApp(activity.packageName)){
                    // Create an intent to add device admin
                    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!")
                    context.startActivity(intent) // Start the activity to request admin permission
                }else{
                    // Show a toast if admin permission is already granted
                    Toast.makeText(context,"the admin permission is add ", Toast.LENGTH_SHORT).show()
                }
            }
        )
        // Overlay permission setting item
        GeneralSettingItem(
            icon = R.drawable.draw, // Icon for overlay permission
            mainText = "Over Draw", // Main text for the item
            subText = "Enable the screen control options", // Subtext for the item
            onClick = {
                Log.d("islam", "GeneralOptionsUISetting : drawAction ")

                // Check if the app does not have overlay permission
                if (!Settings.canDrawOverlays(context)) {
                    // Create an intent to request overlay permission
                    val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Add flag to start activity in a new task
                    context.startActivity(myIntent) // Start the activity to request overlay permission
                } else {
                    // Show a toast if overlay permission is already granted
                    Toast.makeText(context, "Over Draw Already Enabled", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Accessibility service setting item
        GeneralSettingItem(
            icon = R.drawable.ic_baseline_check_24, // Icon for accessibility service
            mainText = "Accessibility Service", // Main text for the item
            subText = "Activate the proactive feature in mobile settings", // Subtext for the item
            onClick = {
                // Get the list of enabled accessibility services
                val enabledServicesSetting = Settings.Secure.getString(context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                )
                // Check if the accessibility service for i-Freeze is not enabled
                if (enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true) {
                    // Create an intent to open accessibility settings
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent) // Start the activity to request accessibility service activation
                } else {
                    // Show a toast if accessibility service is already enabled
                    Toast.makeText(context, "Accessibility service is already enabled", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Location permission setting item
        GeneralSettingItem(
            icon = R.drawable.location, // Icon for location permission
            mainText = "Location Permission", // Main text for the item
            subText = "Permit location accessibility", // Subtext for the item
            onClick = {
                val locationPermissionRequestCode = 123 // Request code for location permission
                // Check if location permission is already granted
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // Request location permission
                    ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationPermissionRequestCode
                    )
                } else {
                    // Show a toast if location permission is already granted
                    Toast.makeText(context, "Location permission is already granted", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Files permission setting item
        GeneralSettingItem(
            icon = R.drawable.folder, // Icon for files permission
            mainText = "Files Permission", // Main text for the item
            subText = "Enable i-Freeze to scan files", // Subtext for the item
            onClick = {
                val EXTERNAL_STORAGE_PERMISSION_CODEE = 1234 // Request code for file permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Check if READ_MEDIA_IMAGES permission is granted
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request READ_MEDIA_IMAGES permission
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                            EXTERNAL_STORAGE_PERMISSION_CODEE
                        )
                    } else {
                        // Show a toast if files access permission is already granted
                        Toast.makeText(context, "Files access was granted", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Check if READ_EXTERNAL_STORAGE permission is granted
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        // Request READ_EXTERNAL_STORAGE permission
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            EXTERNAL_STORAGE_PERMISSION_CODEE
                        )
                    } else {
                        // Show a toast if files access permission is already granted
                        Toast.makeText(context, "Files access was granted", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }
}