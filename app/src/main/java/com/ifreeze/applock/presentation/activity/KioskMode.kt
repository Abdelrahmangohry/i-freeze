package com.ifreeze.applock.presentation.activity

import android.app.Activity
import android.app.ActivityOptions
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.helper.getAppIconByPackageName
import com.ifreeze.applock.helper.toImageBitmap
import com.ifreeze.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.window.DialogProperties


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KioskMode(navController: NavController) {
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    var applicationNames by remember { mutableStateOf(preference.getList("applicationsList")) }
    val deviceManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    val compName = ComponentName(context, MyDeviceAdminReceiver::class.java)
    var isDeviceAdminActive by remember { mutableStateOf(deviceManager.isAdminActive(compName)) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    val view = LocalView.current
    val window = (view.context as Activity).window
    var insetsController = WindowCompat.getInsetsController(window, view)
    var showSwipeAlert by remember { mutableStateOf(false) }


    val predefinedApplicationNames = arrayOf("com.facebook.katana", "com.instagram.android")
    applicationNames = ArrayList(predefinedApplicationNames.toList())

//    LaunchedEffect(Unit) {
//        if (!isDeviceAdminActive) {
//            activateDeviceAdmin(context, deviceManager, compName)
//        } else {
//            startLockTask(context)
////            enterImmersiveMode()
//        }
//    }


    if (showPasswordDialog) {
        PasswordDialog(
            onDismiss = { showPasswordDialog = false },
            onPasswordCorrect = {
                showPasswordDialog = false
                stopLockTask(context)
                navController.popBackStack()
            }
        )
    }

    if (showSwipeAlert) {
        AlertDialog(
            onDismissRequest = { showSwipeAlert = false },
            title = { Text("Swipe Detected") },
            text = { Text("You swiped from down to up!") },
            confirmButton = {
                Button(onClick = { showSwipeAlert = false }) {
                    Text("OK")
                }
            }
        )
    }

    var initialY by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragStart = { offset ->
                        initialY = offset.y
                    },
                    onVerticalDrag = { change, dragAmount ->
                        change.consume()
                        showPasswordDialog = true// Optional: consume the change
                    },
                    onDragEnd = {
                        val finalY = initialY
                        if (initialY - finalY > 100) { // Adjust the threshold as needed
                            showPasswordDialog = true
                        }
                    }
                )

            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF175AA8))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showPasswordDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Applications",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp
                    )
                }

            }

            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(applicationNames.size) { index ->
                    KioskListItems(
                        app = applicationNames[index],

                        onPressAction = { packageName ->
                            openApplication(context, packageName)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }}

    }



private fun startLockTask(context: Context) {
    if (context is ComponentActivity) {
        context.startLockTask()
    }
}

fun stopLockTask(context: Context) {
    if (context is ComponentActivity) {
        context.stopLockTask()
    }
}


private fun activateDeviceAdmin(context: Context, deviceManager: DevicePolicyManager, compName: ComponentName) {
    if (!deviceManager.isAdminActive(compName)) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Enable device admin")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
private fun openApplication(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        context.startActivity(intent)
    } else {
        Toast.makeText(context, "Package not found", Toast.LENGTH_SHORT).show()
    }
}






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordDialog(onDismiss: () -> Unit, onPasswordCorrect: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Password") },
        text = {
            Column (
                modifier = Modifier.fillMaxSize()
            ){
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (password == "123") {
                        onPasswordCorrect()
                    } else {
                        errorMessage = "Incorrect password. Please try again."
                    }
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KioskListItems(app: String, onPressAction: (String) -> Unit) {
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()
    Card(
        onClick= { onPressAction(app) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(shape = Shape.medium)
                    .background(Color(0xFF175AA8))
            ) {
                imageBitmap?.let {
                    Image(
                        bitmap = it,
                        contentDescription = "App Icon",
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = app,
                    color = Color.Black,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                )

            }
        }
    }
}


@Composable
fun LockedSwipePage(
    swipeEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val modifier = if (!swipeEnabled) {
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    change.consumeAllChanges() // Consume all vertical drag gestures
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, _ ->
                    change.consumeAllChanges() // Consume all horizontal drag gestures
                }
            }
    } else {
        Modifier.fillMaxSize()
    }

    Box(modifier = modifier.background(Color.Transparent)) {
        content()
    }
}