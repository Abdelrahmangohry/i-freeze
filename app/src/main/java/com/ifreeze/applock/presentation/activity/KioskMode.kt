package com.ifreeze.applock.presentation.activity

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifreeze.applock.helper.getAppIconByPackageName
import com.ifreeze.applock.helper.toImageBitmap
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway


/**
 * Composable function that displays a list of applications in kiosk mode.
 * The list is populated with application names retrieved from preferences.
 * Clicking on an item will open the corresponding application.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KioskMode(
) {
    val context = LocalContext.current
    // Retrieve list of kiosk applications from preferences
    val preference = PreferencesGateway(context)
    val applicationNames by remember { mutableStateOf(preference.getList("kioskApplications")) }
// Main Column layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8)).padding(vertical = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header text
        Text(
            text = "Applications",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
        // LazyColumn to display list of applications
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(applicationNames.size) { index ->
                // Display each application in the list
                KioskListItems(
                    app = applicationNames[index],

                    onPressAction = { packageName ->
                        // Open the application when an item is clicked
                        openApplication(context, packageName)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}


/**
 * Opens the application with the specified package name.
 * If the application is not found, shows a toast message.
 *
 * @param context The context used to start the activity.
 * @param packageName The package name of the application to be opened.
 */
private fun openApplication(context: Context, packageName: String) {
    val intent = context.packageManager.getLaunchIntentForPackage(packageName)
    if (intent != null) {
        context.startActivity(intent) // Start the application
    } else {
        Toast.makeText(context, "Package not found", Toast.LENGTH_SHORT).show()
    }
}


/**
 * Composable function that represents an item in the kiosk applications list.
 * Displays the application icon and name in a card.
 *
 * @param app The package name of the application.
 * @param onPressAction Lambda function to be called when the item is clicked.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KioskListItems(app: String, onPressAction: (String) -> Unit) {
    // Get the app icon as a bitmap
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()
    // Card layout for the application item
    Card(
        onClick = { onPressAction(app) },
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {
        // Row layout inside the card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Box to display the application icon
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(shape = Shape.medium)
                    .background(Color(0xFF175AA8))
            ) {
                // Display the app icon if available
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
                // Text to display the application name
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
