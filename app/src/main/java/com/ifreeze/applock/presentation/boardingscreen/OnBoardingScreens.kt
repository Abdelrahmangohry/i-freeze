package com.ifreeze.applock.presentation.boardingscreen

import android.Manifest
import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.presentation.activity.GeneralSettingItem
import com.ifreeze.applock.presentation.activity.HeaderLogo
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.presentation.screen.PermissionViewModel
import com.ifreeze.applock.ui.theme.Shape
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun OnboardingScreen1(navController: NavHostController) {
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
        pushStringAnnotation(tag = "Privacy Policy", annotation = "https://flothers.com/privacy_policy")
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderLogo()
            Spacer(modifier = Modifier.height(24.dp))
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                onClick = { navController.navigate(Screen.OnboardingScreen2.route) },
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text(text = "GET STARTED", color = Color.White)
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
            Spacer(modifier = Modifier.width(20.dp))
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
                subText = "Enable the screen control option in settings",
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
                subText = "Enable the screen control option in settings",
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
    val currentPage = remember { mutableStateOf(3) }
Column(
    modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8)),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
) {
    HeaderLogo()
    OnboardingImage(R.drawable.welcome_page)

    Text(modifier = Modifier.padding(vertical =5.dp ), text = "Welcome to i-Freeze", color = Color.White, fontSize = 25.sp)
    Text(modifier = Modifier.padding(vertical =5.dp, horizontal = 15.dp ), text = "You can now manage and protect your mobile device", color = Color.White, fontSize = 22.sp)
    Button(modifier = Modifier.padding(vertical = 10.dp), colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)), onClick = {navController.navigate(Screen.AdminAccess.route)}){
        Text(text = "Start Application", color = Color.White)

    }
}

}

@Composable
fun OnboardingScreen(
    imageRes: Int,
    text: AnnotatedString,
    buttonText: String,
    onTextClick: (Int) -> Unit,
    onButtonClick: () -> Unit,
    currentPage: Int,
    totalPages: Int,
    onIndicatorClick: (Int) -> Unit,
    additionalContent: @Composable () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8)),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OnboardingImage(imageRes = imageRes)
            Spacer(modifier = Modifier.height(24.dp))
            ClickableText(
                text = text,
                style = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                ),
                onClick = { offset -> onTextClick(offset) }
            )
            additionalContent()
        }

//        PageIndicator(
//            totalPages = totalPages,
//            currentPage = currentPage,
//            onIndicatorClick = onIndicatorClick
//        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = onButtonClick,
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
        ) {
            Text(text = buttonText, color = Color.White)
        }
    }

}

@Composable
fun PageIndicator(totalPages: Int, currentPage: Int, onIndicatorClick: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        repeat(totalPages) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .size(16.dp)
                    .background(
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                        shape = MaterialTheme.shapes.small
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    )
                    .clickable { onIndicatorClick(index) }
            )
        }
    }
}

@Composable
fun OnboardingImage(imageRes: Int) {
    Image(
        painter = painterResource(id = imageRes),
        contentDescription = null,
        modifier = Modifier
            .fillMaxHeight(0.50f).fillMaxWidth().padding(15.dp),
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
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = { onClick() },
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 14.dp)
                    .fillMaxWidth(),
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
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))

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

                Icon(
                    imageVector = icon2,
                    contentDescription = null,
                    tint = if (granted) Color.Green else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun NumberedCircle(number: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(40.dp)  // Size of the circle
            .background(
                color = Color.White,
                shape = CircleShape
            ) // Circle shape with white background

    ) {
        Text(
            text = number.toString(),
            color = Color(0xFF175AA8),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}