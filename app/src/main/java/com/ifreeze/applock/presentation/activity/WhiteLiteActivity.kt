package com.ifreeze.applock.presentation.activity

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getAppIconByPackageName
import com.ifreeze.applock.helper.toImageBitmap
import com.ifreeze.applock.presentation.AppsViewModel
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WhiteList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController) {
    // Get the preferences gateway to handle storing and retrieving allowed apps
    val preference = PreferencesGateway(LocalContext.current)
    // Remember the allowed apps list from preferences or initialize as an empty list if not found
    var allowedAppsList by remember { mutableStateOf(preference.getList("allowedAppsList") ?: mutableListOf()) }
    // State for handling input text in the text field
    var inputText by remember { mutableStateOf("") }
    // Main container with a blue background
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        // Top row containing the back button and the title
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) {
            // Back button to navigate to the previous screen
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            // Title text for the screen
            Text(
                text = "Whitelisted Applications",
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 15.dp)
                    .padding(horizontal = 30.dp),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp
            )
        }
// Column containing the text input and submit button
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
        ) {
            // Text field for entering the package name
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.fillMaxWidth()
                    .background(color = Color.White)
                    .padding(15.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black, // Text color // Color of the leading icon
                    unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                    focusedBorderColor = Color.Black,
                    cursorColor = Color.Black,
                ),
                maxLines = 1,
                label = { Text(text = "Package Name", color = Color.Black) },
                placeholder = { Text(text = "Enter Package Name") },
            )
            // Button to submit the entered package name
            Button(
                onClick = {
                    if (inputText.isNotEmpty()) {
                        // Add the entered package name to the allowed apps list
                        allowedAppsList =
                            allowedAppsList.toMutableList().apply { add(inputText.lowercase().trim()) }
                        // Save the updated list in preferences
                        preference.saveList("allowedAppsList", allowedAppsList)
                        // Clear the input text field
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Submit", color = Color.White)
            }
        }
        // LazyColumn to display the list of allowed apps
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            // Iterate over the allowed apps list
            items(allowedAppsList.size) { index ->
                // Display each app in the list using the AppListItemWhiteList composable
                AppListItemWhiteList(
                    app = allowedAppsList[index],
                    onDeleteClick = {
                        // Handle deletion of the selected app
                        allowedAppsList = allowedAppsList.toMutableList().apply {
                            remove(allowedAppsList[index])
                        }
                        // Save the updated list in preferences
                        preference.saveList("allowedAppsList", allowedAppsList)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun AppListItemWhiteList(app: String, onDeleteClick: () -> Unit) {
    // Retrieve the app icon using the package name and convert it to a bitmap
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()
    // Card container for each app item
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {
        // Row containing the app icon and name with a delete button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Container for the app icon
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
            // Row containing the app name and the delete button
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Display the app name
                Text(
                    text = app,
                    color = Color.Black,
                    modifier = Modifier.weight(1f).padding(8.dp),
                )
                // Button to delete the app from the list
                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.size(30.dp).padding(end = 8.dp),

                    ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Black
                    )
                }

            }

        }
    }
}
