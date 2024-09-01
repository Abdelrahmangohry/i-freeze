package com.ifreeze.applock.presentation.activity

import androidx.annotation.RequiresApi
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway

/**
 * Composable function for managing blacklisted websites.
 * Displays a list of blocked websites and allows adding or removing websites from the blacklist.
 *
 * @param navController Controls navigation between screens.
 */
@RequiresApi(34)
@Composable
fun BlackListWeb(navController: NavController) {
    // Initialize preferences gateway to load and save settings.
    val preference = PreferencesGateway(LocalContext.current)
    // Main container for the UI, with a background color and full size.
    Column(
        modifier = Modifier
            .background(Color(0xFF175AA8))
            .fillMaxSize()
    ) {
        // Nested column to arrange the components with padding.
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp),
        ) {
            // Display the header with a back button.
            webBlackListTitle(onBackPressed = { navController.popBackStack() })
            // State to hold the list of blocked websites, initialized from preferences.
            var blockedWebsites by remember {
                mutableStateOf(
                    preference.getList("blockedWebsites") ?: mutableListOf()
                )
            }
            // Display input field and submit button for adding new websites to the blacklist.
            textWithButtonBlackListWebView(
                onValidMacSubmit = { value ->
                    // Add the new website to the list and save it to preferences.
                    blockedWebsites =
                        blockedWebsites.toMutableList().apply { add(value.lowercase().trim()) }
                    preference.saveList("blockedWebsites", blockedWebsites)
                },
                blockedWebsites = blockedWebsites
            )
            // Display the list of blocked websites using LazyColumn.
            LazyColumn(
                modifier = Modifier.fillMaxWidth() // Fill the width of the parent container.
            ) {
                // Iterate over the list of blocked websites and display each one.
                items(blockedWebsites) { website ->
                    ListItemBlackListWeb(
                        webSites = WebSiteBlack(name = website),
                        onDeleteClick = {
                            // Handle delete action for the specific website.
                            blockedWebsites =
                                blockedWebsites.filterNot { it == website }.toMutableList()
                            preference.saveList("blockedWebsites", blockedWebsites)

                        }
                    )
                }
            }

//            Log.d("abd", "ssidList items: ${ssidList.joinToString()}")
        }
    }
}

/**
 * Data class to represent a blacklisted website with optional icon.
 *
 * @param name The name of the website.
 * @param icon Optional icon for the website.
 */
data class WebSiteBlack(val name: String, val icon: String? = null)


/**
 * Composable function to display a list item for a blacklisted website.
 *
 * @param webSites The blacklisted website data to display.
 * @param onDeleteClick Lambda function called when the delete icon is clicked.
 */
@Composable
fun ListItemBlackListWeb(webSites: WebSiteBlack, onDeleteClick: () -> Unit) {
    // Card container for the list item with background color and padding.
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF175AA8))
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
        shape = Shape.large
    ) {
        // Row to arrange the content horizontally.
        Row(
            modifier = Modifier.fillMaxSize().background(Color.White).padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Display the website name.
            Text(
                text = webSites.name,
                modifier = Modifier.weight(1f).padding(8.dp),
                color = Color.Black
            )
            // IconButton for deleting the website from the blacklist.
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(30.dp).padding(end = 8.dp),

                ) {
                // Delete icon inside the button.
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
 * Composable function to display an input field and a submit button for adding websites to the blacklist.
 *
 * @param onValidMacSubmit Lambda function called when a valid website is submitted.
 * @param blockedWebsites List of currently blocked websites.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textWithButtonBlackListWebView(
    onValidMacSubmit: (String) -> Unit,
    blockedWebsites: List<String>
) {
    // State to hold the value of the input field.
    val text2 = remember { mutableStateOf("") }
// Column to arrange the input field and button vertically.
    Column(modifier = Modifier.fillMaxWidth()) {
        // Outlined text field for entering new websites.
        OutlinedTextField(
            value = text2.value, // Current value of the text field.
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.White)
                .padding(15.dp),

            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.Black, // Text color // Color of the leading icon
                unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                focusedBorderColor = Color.Black,
                cursorColor = Color.Black,
            ),
            maxLines = 1, // Limit to one line of text.
            onValueChange = { newText ->
                // Update the text value on change.
                text2.value = newText
            },
            label = { Text(text = "Address", color = Color.Black) }, // Label text.
            placeholder = { Text(text = "Enter Address") }, // Placeholder text.
        )
        // Button for submitting the new website address.
        Button(
            onClick = {
                val newWebsite = text2.value.trim() // Get trimmed text from the input field.
                if (newWebsite.isNotEmpty() && newWebsite !in blockedWebsites) {
                    onValidMacSubmit(newWebsite) // Call the submission function.
                    text2.value = "" // Clear the text field
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
}

/**
 * Composable function to display the title for the blacklist screen with a back button.
 *
 * @param onBackPressed Lambda function called when the back button is pressed.
 */
@Composable
fun webBlackListTitle(onBackPressed: () -> Unit) {
    // Row to arrange back button and title horizontally.
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        // Icon button for navigating back.
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        // Title text for the blacklist screen.
        Text(
            text = "Blacklisted Websites",
            color = Color.White,

            modifier = Modifier
                .fillMaxWidth() // Text takes up full width.
                .padding(top = 6.dp, bottom = 30.dp).padding(horizontal = 35.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}
