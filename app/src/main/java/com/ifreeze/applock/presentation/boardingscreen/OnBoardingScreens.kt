package com.ifreeze.applock.presentation.boardingscreen

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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
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
import com.ifreeze.applock.ui.theme.Shape


@Composable
fun OnboardingScreen1(navController: NavHostController) {
    val annotatedText = buildAnnotatedString {
        append("By proceeding, you confirm ")
        pushStringAnnotation(tag = "Agreement", annotation = "https://www.google.com")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        ) {
            append("Agreement")
        }
        pop()
        append(" and ")
        pushStringAnnotation(tag = "Privacy Policy", annotation = "https://www.youtube.com")
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
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
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderLogo()
            Spacer(modifier = Modifier.height(24.dp))
            ClickableText(
                text = annotatedText,
                style = LocalTextStyle.current.copy(
                    fontSize = 18.sp,
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
            //additionalContent()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
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
            OnboardingImage(R.drawable.mob)

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
                Text(
                    text = "1. Open Accessibility settings by tapping the setting button below",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "2. Tap Installed apps or Installed services and select i-Freeze Antivirus",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "3. Tap the toggle to give us permission",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Button(colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
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
                Text(text = "SETTINGS", color = Color.White)
            }
            Button(colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
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
    val currentPage = remember { mutableStateOf(2) }
    val context = LocalContext.current
    val deviceManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    val nonGrantedPermissions = remember { mutableListOf<String>() }

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
            GeneralSettingItemNew(
                icon = R.drawable.admin,
                mainText = "Admin Permission",
                subText = "Provide admin privilege to i-Freeze",
                onClick = {
                    if (!deviceManager.isAdminActive(compName)) {
                        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
                        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
                        intent.putExtra(
                            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "You should enable the app!"
                        )
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(
                            context,
                            "Admin permission is already granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                granted = deviceManager.isAdminActive(compName)
            )

            GeneralSettingItemNew(
                icon = R.drawable.draw,
                mainText = "Over Draw",
                subText = "Enable the screen control option in settings",
                onClick = {
                    if (!Settings.canDrawOverlays(context)) {
                        val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(myIntent)
                    } else {
                        Toast.makeText(context, "Over Draw is already enabled", Toast.LENGTH_SHORT)
                            .show()
                    }
                },
                granted = Settings.canDrawOverlays(context)
            )

            GeneralSettingItemNew(
                icon = R.drawable.locked_icon,
                mainText = "Install Unknown Apps",
                subText = "Enable the screen control option in settings",
                onClick = {
                    if (!context.packageManager.canRequestPackageInstalls()) {
                        val settingsIntent =
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                        context.startActivity(settingsIntent)
                    } else {
                        Toast.makeText(
                            context,
                            "Install Unknown Apps is already enabled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                granted = context.packageManager.canRequestPackageInstalls()
            )

            GeneralSettingItemNew(
                icon = R.drawable.location,
                mainText = "Location Permission",
                subText = "Permit location accessibility",
                onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            123
                        )
                    } else {
                        Toast.makeText(
                            context,
                            "Location permission is already granted",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                granted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )

            GeneralSettingItemNew(
                icon = R.drawable.folder,
                mainText = "Files Permission",
                subText = "Enable i-Freeze to scan files",
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
                                1234
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Files access is already granted",
                                Toast.LENGTH_SHORT
                            ).show()
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
                                1234
                            )
                        } else {
                            Toast.makeText(
                                context,
                                "Files access is already granted",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                granted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)),
            onClick = {
                // Check permissions
                nonGrantedPermissions.clear()

                if (!deviceManager.isAdminActive(compName)) {
                    nonGrantedPermissions.add("Admin Permission")
                }
                if (!Settings.canDrawOverlays(context)) {
                    nonGrantedPermissions.add("Over Draw")
                }
                if (!context.packageManager.canRequestPackageInstalls()) {
                    nonGrantedPermissions.add("Install Unknown Apps")
                }
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    nonGrantedPermissions.add("Location Permission")
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_MEDIA_IMAGES
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        nonGrantedPermissions.add("Files Permission")
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        nonGrantedPermissions.add("Files Permission")
                    }
                }

                if (nonGrantedPermissions.isEmpty()) {
                    navController.navigate(Screen.OnboardingScreen4.route)
                } else {
                    Toast.makeText(
                        context,
                        "Please grant the following permissions: ${nonGrantedPermissions.joinToString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }

            }) {
            Text(text = "Next", color = Color.White)
        }
    }
}

@Composable
fun OnboardingScreen4(navController: NavHostController) {
    val currentPage = remember { mutableStateOf(3) }

    Text(text = "Fourth Page")
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
            .fillMaxHeight(0.35f).fillMaxWidth().padding(50.dp),
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
    granted: Boolean
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
                    imageVector = if (granted) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (granted) Color.Green else Color.Red
                )
            }
        }
    }
}