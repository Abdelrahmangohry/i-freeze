package com.ifreeze.applock.boardingscreen

import android.Manifest
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.presentation.activity.HeaderLogo
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.presentation.screen.PermissionViewModel
import com.ifreeze.applock.ui.theme.Shape
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingScreen1(navController: NavHostController) {
    // Annotated string for clickable text links to "Agreement" and "Privacy Policy"
    val annotatedText = buildAnnotatedString {
        append("By proceeding, you confirm ")
        pushStringAnnotation(tag = "Agreement", annotation = "https://flothers.com/user_agreement")
        withStyle(
            style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Agreement")
        }
        pop()
        append(" and ")
        pushStringAnnotation(
            tag = "Privacy Policy",
            annotation = "https://flothers.com/privacy_policy"
        )
        withStyle(
            style = SpanStyle(
                color = Color.White,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Privacy Policy")
        }
        pop()
    }
    // Main layout for the first onboarding screen
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8)), // Set background color
        verticalArrangement = Arrangement.SpaceBetween, // Space elements vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center elements horizontally
    ) {
        // Header and clickable text
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.Center, // Center the content vertically
            horizontalAlignment = Alignment.CenterHorizontally // Center the content horizontally
        ) {
            HeaderLogo() // Custom composable for the header logo
            Spacer(modifier = Modifier.height(24.dp)) // Space between logo and text

            // Clickable text with links to "Agreement" and "Privacy Policy"
            ClickableText(
                text = annotatedText,
                style = LocalTextStyle.current.copy(
                    fontSize = 20.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                onClick = { offset ->
                    annotatedText.getStringAnnotations(start = offset, end = offset)
                        .firstOrNull()?.let { annotation ->
                            // Open the corresponding link when text is clicked
                            navController.context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(annotation.item)
                                )
                            )
                        }
                }
            )
        }

        // Button to navigate to the next screen
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,// Center the button horizontally
            verticalAlignment = Alignment.CenterVertically // Align button content vertically
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp), // Full-width button with padding
                onClick = { navController.navigate(Screen.OnboardingScreen2.route) }, // Navigate to Screen 2
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)) // Button color styling
            ) {
                Text(text = "GET STARTED", color = Color.White) // Button text
            }
        }
    }
}


@Composable
fun OnboardingScreen2(navController: NavHostController) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(2f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingImage(R.drawable.accessnew)

            Text(
                modifier = Modifier.padding(15.dp),
                text = "Enable accessibility service in settings for keeping your mobile safe.",
                color = Color.White,
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column(
                modifier = Modifier.fillMaxWidth().padding(15.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberedCircle(1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Open Accessibility settings by tapping the setting button below",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberedCircle(2)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap Installed apps or Installed services and select i-Freeze Antivirus",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberedCircle(3)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap the toggle to give us permission",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
                onClick = {
                    val enabledServicesSetting = Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    )
                    if (enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true) {
                        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                        context.startActivity(intent)
                        Log.d("abdo", "Setting Not Granted")
                    } else {
                        Toast.makeText(
                            context,
                            "Accessibility permission is already granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) {
                Text(text = "Settings", color = Color.White)
            }
            Button(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
                onClick = {
                    val enabledServicesSetting = Settings.Secure.getString(
                        context.contentResolver,
                        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                    )
                    if (enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true) {
                        Toast.makeText(
                            context,
                            "Enable accessibility service to proceed",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        navController.navigate(Screen.OnboardingScreen3.route)
                        Log.d("abdo", "Setting Granted")
                    }
                }) {
                Text(text = "Next", color = Color.White)
            }

        }
    }
}

@Composable
fun OnboardingScreen3(navController: NavHostController) {
    val context = LocalContext.current
    val deviceManager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(context, MyDeviceAdminReceiver::class.java)

    // ViewModel to manage permission state
    val viewModel: PermissionViewModel = viewModel()

    // Observe permission states
    val permissionStates by viewModel.permissionStates.collectAsState()
    viewModel.checkAllPermissions(context, compName, deviceManager)
    // Define UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OnboardingImage(R.drawable.accessabilitypref)
            // Admin Permission Card
            GeneralSettingItemNew(
                icon = R.drawable.admin,
                mainText = "Admin Permission",
                subText = "Provide admin privilege to i-Freeze",
                onClick = {
                    if (!deviceManager.isAdminActive(compName)) {
                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
                            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "You should enable the app!")
                        }
                        context.startActivity(intent)

                    } else {
                        Toast.makeText(context, "Admin permission is already granted", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.checkAllPermissions(context, compName, deviceManager)
                },
                granted = permissionStates.adminPermission,
                icon2 = if (permissionStates.adminPermission) Icons.Default.CheckCircle else Icons.Default.TouchApp
            )

            // Over Draw Permission Card
            GeneralSettingItemNew(
                icon = R.drawable.draw,
                mainText = "Over Draw",
                subText = "Enable the screen control options",
                onClick = {
                    if (!Settings.canDrawOverlays(context)) {
                        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(myIntent)
                    } else {
                        Toast.makeText(context, "Over Draw is already enabled", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.checkAllPermissions(context, compName, deviceManager)
                },
                granted = permissionStates.drawOverPermission,
                icon2 = if (permissionStates.drawOverPermission) Icons.Default.CheckCircle else Icons.Default.TouchApp
            )

            // Install Unknown Apps Permission Card
            GeneralSettingItemNew(
                icon = R.drawable.locked_icon,
                mainText = "Install Unknown Apps",
                subText = "Enable the screen control options",
                onClick = {
                    if (!context.packageManager.canRequestPackageInstalls()) {
                        val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(settingsIntent)
                    } else {
                        Toast.makeText(context, "Install Unknown Apps is already enabled", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.checkAllPermissions(context, compName, deviceManager)
                },
                granted = permissionStates.unknownAppsPermission,
                icon2 = if (permissionStates.unknownAppsPermission) Icons.Default.CheckCircle else Icons.Default.TouchApp
            )

            // Location Permission Card
            GeneralSettingItemNew(
                icon = R.drawable.location,
                mainText = "Location Permission",
                subText = "Permit location accessibility",
                onClick = {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 123)
                    } else {
                        Toast.makeText(context, "Location permission is already granted", Toast.LENGTH_SHORT).show()
                    }
                    viewModel.checkAllPermissions(context, compName, deviceManager)
                },
                granted = permissionStates.locationPermission,
                icon2 = if (permissionStates.locationPermission) Icons.Default.CheckCircle else Icons.Default.TouchApp
            )

            // Files Permission Card
            GeneralSettingItemNew(
                icon = R.drawable.folder,
                mainText = "Files Permission",
                subText = "Enable i-Freeze to scan files",
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 1234)
                        } else {
                            Toast.makeText(context, "Files access is already granted", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1234)
                        } else {
                            Toast.makeText(context, "Files access is already granted", Toast.LENGTH_SHORT).show()
                        }
                    }
                    viewModel.checkAllPermissions(context, compName, deviceManager)
                },
                granted = permissionStates.filesPermission,
                icon2 = if (permissionStates.filesPermission) Icons.Default.CheckCircle else Icons.Default.TouchApp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Next Button
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
            onClick = {
                val nonGrantedPermissions = viewModel.checkAllPermissions(context, compName, deviceManager)
                if (nonGrantedPermissions.isEmpty()) {
                    navController.navigate(Screen.OnboardingScreen4.route)
                } else {
                    Toast.makeText(
                        context,
                        "Please grant the following permissions: ${nonGrantedPermissions.joinToString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text(text = "Next", color = Color.White)
        }
    }
}


@Composable
fun OnboardingScreen4(navController: NavHostController) {
    // Main layout for the fourth onboarding screen

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8)), // Set background color
        verticalArrangement = Arrangement.Center, // Center content vertically
        horizontalAlignment = Alignment.CenterHorizontally  // Center elements horizontally
    ) {
        // ifreeze logo
        HeaderLogo()


        OnboardingImage(R.drawable.welcome_page)

        Text(
            modifier = Modifier.padding(vertical = 5.dp),
            text = "Welcome to i-Freeze",
            color = Color.White,
            fontSize = 25.sp
        )
        Text(
            modifier = Modifier.padding(vertical = 5.dp).fillMaxWidth(),
            text = "You can now manage and protect your mobile device",
            color = Color.White,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(25.dp))
        Button(
            modifier = Modifier.padding(vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
            onClick = { navController.navigate(Screen.AdminAccess.route) }) {
            Text(text = "Start Application", color = Color.White)

        }
    }

}

@Composable
fun OnboardingImage(imageRes: Int) {
    // Display an image using the provided resource ID
    Image(
        // Load the image resource using the painterResource function
        painter = painterResource(id = imageRes),
        // No content description is provided as it's not needed in this context
        contentDescription = null,
        // Set the image to fill half the height of its parent container
        modifier = Modifier
            .fillMaxHeight(0.50f)
            // Set the image to fill the entire width of its parent container
            .fillMaxWidth()
            // Add padding around the image
            .padding(15.dp),
        // Scale the image to fit within its container while maintaining aspect ratio
        contentScale = ContentScale.Fit
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingItemNew(
    icon: Int,
    mainText: String,
    subText: String,
    onClick: () -> Unit,
    granted: Boolean,
    icon2: ImageVector
) {
    // Create an elevated card with a click action
    ElevatedCard(
        // Set the elevation of the card to 8.dp
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        // Trigger the onClick function when the card is clicked
        onClick = { onClick() },
        // Make the card fill the full width of its parent and add bottom padding
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxWidth()
    ) {
        // Create a box to hold the content, filling the full width of the card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                // Set the background color of the card to white
                .background(Color.White)
        ) {
            // Create a horizontal row to arrange items inside the card
            Row(
                modifier = Modifier
                    // Add padding to the row's vertical and horizontal sides
                    .padding(vertical = 10.dp, horizontal = 14.dp)
                    .fillMaxWidth(),
                // Align items vertically centered within the row
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Create a box to hold the icon, set its size and shape
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        // Clip the box to a medium shape
                        .clip(shape = Shape.medium)
                        // Set the background color of the icon box
                        .background(Color(0xFF175AA8))
                ) {
                    // Display the icon inside the box with padding
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        tint = Color.White,
                        // Add padding inside the icon box
                        modifier = Modifier.padding(8.dp)
                    )
                }

                // Add a spacer between the icon and text
                Spacer(modifier = Modifier.width(9.dp))

                // Create a column to hold the main and subtext
                Column(
                    // Make the column take up the remaining space in the row
                    modifier = Modifier.weight(1f)
                ) {
                    // Display the main text with styling
                    Text(
                        text = mainText,
                        color = Color(0xFF175AA8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    // Display the subtext with styling
                    Text(
                        text = subText,
                        color = Color(0xFF175AA8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                // Display the second icon, color it based on the granted status
                Icon(
                    imageVector = icon2,
                    contentDescription = null,
                    // Change tint to green if granted, otherwise dark gray
                    tint = if (granted) Color.Green else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun NumberedCircle(number: Int) {
    // Create a box with centered content to display a number inside a circle
    Box(
        contentAlignment = Alignment.Center,
        // Set the size of the circle
        modifier = Modifier
            .size(40.dp)
            // Set the background color to white and shape to a circle
            .background(
                color = Color.White,
                shape = CircleShape
            )
    ) {
        // Display the number in the center of the circle with styling
        Text(
            text = number.toString(),
            color = Color(0xFF175AA8),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}