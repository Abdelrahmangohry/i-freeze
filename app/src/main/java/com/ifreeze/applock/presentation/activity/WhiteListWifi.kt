package com.ifreeze.applock.presentation.activity

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.ui.theme.Shape

import com.ifreeze.data.cash.PreferencesGateway

@Composable
fun WhiteListWifi(navController: NavController) {
    // Initialize preferences gateway to manage saved lists in SharedPreferences
    val preference = PreferencesGateway(LocalContext.current)
    // Main Column for the entire UI layout with a blue background
    Column(
        modifier = Modifier
            .background(Color(0xFF175AA8))
            .fillMaxSize()
    ) {
        // Inner Column for padding and layout management
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp),
        ) {
            // Header menu with a back button and title
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Whitelisted WiFi")
            // State variable to hold the list of allowed WiFi names
            var allowedWifi by remember {
                mutableStateOf(
                    preference.getList("allowedWifiList") ?: mutableListOf()
                )

            }
            // Input field and button for adding new allowed WiFi names
            SimpleInputTextWithButtonWifi(
                onValidMacSubmit = { value ->
                    // Update allowedWifi list with the new value
                    allowedWifi =
                        allowedWifi.toMutableList().apply { add(value.lowercase().trim()) }
                    preference.saveList("allowedWifiList", allowedWifi)
                },
                allowedWifi = allowedWifi // Pass allowedWifi here
            )
            // LazyColumn to display the list of allowed WiFi names
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allowedWifi) { wifi ->
                    listItemWifi(

                        wifi = WifiData(name = wifi),
                        onDeleteClick = {
                            // Handle delete action: remove the WiFi name from the list
                            allowedWifi = allowedWifi.filterNot { it == wifi }.toMutableList()
                            preference.saveList("allowedWifiList", allowedWifi)
                            Log.d("abdo", "allowWifi $allowedWifi")
                        }
                    )
                }
            }

        }
    }
}

// Data class to represent a WiFi item with a name and an optional icon.
data class WifiData(val name: String, val icon: String? = null)

@Composable
fun listItemWifi(wifi: WifiData, onDeleteClick: () -> Unit) {
    // Card representing a WiFi item with a delete button.
    Card(
        modifier = Modifier
            .fillMaxWidth() // Makes the card take up the full width.
            .background(Color(0xFF175AA8)) // Background color of the card.
            .padding(horizontal = 10.dp) // Adds horizontal padding around the card.
            .padding(top = 10.dp), // Adds padding at the top of the card.
        shape = Shape.large // Sets the shape of the card.
    ) {
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Displays the WiFi name.
            Text(
                text = wifi.name,
                modifier = Modifier.weight(1f).padding(8.dp),
                color = Color.Black
            )
            // Delete button to remove the WiFi name from the list.
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(30.dp).padding(end = 8.dp),

                ) {
                // Icon for the delete button.v
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Black
                )
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleInputTextWithButtonWifi(onValidMacSubmit: (String) -> Unit, allowedWifi: List<String>) {
    // State to hold the current input value.
    val text2 = remember { mutableStateOf("") }
    // Column containing the input field and submit button.
    Column(modifier = Modifier.fillMaxWidth()) {
        // Input field for entering a WiFi name.
        OutlinedTextField(
            value = text2.value, // Binds the input value to the state.
            modifier = Modifier
                .fillMaxWidth() // Makes the text field take up the full width.
                .background(color = Color.White) // Sets the background color to white.
                .padding(15.dp), // Adds padding around the text field.

            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black, // Text color // Color of the leading icon
                unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                focusedBorderColor = Color.Black,
                cursorColor = Color.Black,
            ),
            maxLines = 1, // Limits the text field to one line.
            onValueChange = { newText ->
                text2.value = newText // Updates the state with the new input.
            },
            label = { Text(text = "Wifi", color = Color.Black) }, // Label for the text field.
            placeholder = { Text(text = "Enter Wifi Name", color = Color.Gray) }, // Placeholder text.

            )
        // Button to submit the WiFi name.
        Button(
            onClick = {
                val wifiName = text2.value.trim() // Trims whitespace from the input.
                if (wifiName.isNotEmpty() && wifiName !in allowedWifi) { // Checks if the input is valid.
                    onValidMacSubmit(wifiName) // Submits the WiFi name.
                    text2.value = "" // Clears the input field.
                }
            },
            modifier = Modifier
                .fillMaxWidth()  // Makes the button take up the full width.
                .padding(15.dp),  // Adds padding around the button.
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)) // Sets the button color.
        ) {
            Text("Submit", color = Color.White) // Text inside the button.
        }
    }
}

