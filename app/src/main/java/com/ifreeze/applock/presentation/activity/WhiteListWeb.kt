package com.ifreeze.applock.presentation.activity

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


/**
 * Composable function that represents the Whitelist Websites screen. This screen allows
 * the user to add websites to a whitelist and manage them by removing unwanted entries.
 *
 * @param navController The NavController used to manage navigation between different
 * screens in the app.
 */
@Composable
fun WhiteListWeb(navController: NavController) {
    // Initialize PreferencesGateway to load and save preferences
    val preference = PreferencesGateway(LocalContext.current)

    // Main container for the WhiteListWeb screen
    Column(
        modifier = Modifier
            .background(Color(0xFF175AA8))
            .fillMaxSize()
    ) {
        // Secondary container to hold the header and content
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp),
        ) {
            // Header with back button and screen title
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Whitelisted Websites")

            // State for storing the list of allowed websites
            var allowedWebsites by remember {
                mutableStateOf(
                    preference.getList("allowedWebsites") ?: mutableListOf()
                )
            }
            // Custom composable for input text field and submit button
            textWithButtonWhiteListWebView(onValidMacSubmit = { value ->
                // Add the new website to the list and save it in preferences
                allowedWebsites =
                    allowedWebsites.toMutableList().apply { add(value.lowercase().trim()) }
                preference.saveList("allowedWebsites", allowedWebsites)
            })
            // Display the list of whitelisted websites using LazyColumn
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allowedWebsites) { website ->
                    // Custom composable for each website in the whitelist
                    ListItemWhiteListWeb(
                        webSitesWhite = WebSiteWhiteList(name = website),
                        onDeleteClick = {
                            // Handle delete action to remove website from the list
                            allowedWebsites =
                                allowedWebsites.filterNot { it == website }.toMutableList()
                            preference.saveList("allowedWebsites", allowedWebsites)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Data class representing a website in the whitelist.
 *
 * @param name The name or URL of the website.
 * @param icon Optional icon representing the website.
 */
data class WebSiteWhiteList(val name: String, val icon: String? = null)


/**
 * Composable function that represents an item in the whitelist of websites.
 * This function displays the website name and provides a delete button to
 * remove the website from the list.
 *
 * @param webSitesWhite The WebSiteWhiteList object containing the website name.
 * @param onDeleteClick Lambda function invoked when the delete button is clicked.
 */
@Composable
fun ListItemWhiteListWeb(webSitesWhite: WebSiteWhiteList, onDeleteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display the website name
            Text(
                text = webSitesWhite.name,
                modifier = Modifier.weight(1f).padding(8.dp),
                color = Color.Black
            )
            // Delete button to remove the website from the list
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

/**
 * Composable function that represents an input field with a submit button for
 * adding websites to the whitelist. The entered website is added to the whitelist
 * when the submit button is clicked.
 *
 * @param onValidMacSubmit Lambda function invoked when a valid website is submitted.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textWithButtonWhiteListWebView(onValidMacSubmit: (String) -> Unit) {
    // State for storing the text entered by the user
    val text2 = remember { mutableStateOf("") }
    // Container for the input field and submit button
    Column(modifier = Modifier.fillMaxWidth()) {
        // Outlined text field for entering the website address
        OutlinedTextField(
            value = text2.value,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(15.dp),

            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black,  // Set the text color
                unfocusedBorderColor = Color.LightGray,  // Border color when unfocused
                focusedBorderColor = Color.Black,  // Border color when focused
                cursorColor = Color.Black  // Cursor color
            ),
            maxLines = 1, // Maximum number of lines for the input
            onValueChange = { newText ->
                text2.value = newText // Update the state with the new text
            },
            label = { Text(text = "Address", color = Color.Black) }, // Label for the text field
            placeholder = { Text(text = "Enter Address", color = Color.Black) }, // Placeholder text
        )
        // Submit button to add the entered website to the whitelist
        Button(
            onClick = {
                val newWebsite = text2.value.trim()
                if (newWebsite.isNotEmpty()) {
                    // Add the website if the input is not empty
                    onValidMacSubmit(newWebsite)
                    text2.value = "" // Clear the text field after submission
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)) // Button color
        ) {
            Text("Submit", color = Color.White)
        }
    }
}


