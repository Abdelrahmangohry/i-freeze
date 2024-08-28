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
 import androidx.navigation.NavController
 import com.ifreeze.applock.R
 import com.ifreeze.applock.helper.getAppIconByPackageName
 import com.ifreeze.applock.helper.toImageBitmap
 import com.ifreeze.applock.ui.theme.Shape
 import com.patient.data.cashe.PreferencesGateway


 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun BlackList(navController: NavController) {
     // Access the PreferencesGateway to retrieve and save blocked apps list
     val preference = PreferencesGateway(LocalContext.current)
     // State variable to hold the list of blocked apps, initially populated from preferences
     var blockedApps by remember { mutableStateOf(preference.getList("blockedAppsList")) }
     // State variable to hold the text input for the package name
     var inputText by remember { mutableStateOf("") }

     Column(
         modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
     ) {
         // Header Row with a back button and title
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
                 text = "Blacklisted Application",
                 color = Color.White,
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(top = 6.dp, bottom = 15.dp)
                     .padding(horizontal = 30.dp),
                 fontWeight = FontWeight.ExtraBold,
                 fontSize = 22.sp
             )
         }

         // Column to hold the text field and button
         Column(
             modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp) // Sets padding on the horizontal sides
         ) {
             // Text field for entering the package name to be blocked
             OutlinedTextField(
                 value = inputText, // Binds the text field value to the inputText state variable
                 onValueChange = { inputText = it }, // Updates inputText when the user types
                 modifier = Modifier.fillMaxWidth()
                     .background(color = Color.White) // Sets background color to white
                     .padding(15.dp), // Adds padding inside the text field
                 colors = TextFieldDefaults.outlinedTextFieldColors(
                     textColor = Color.Black, // Sets text color
                     unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                     focusedBorderColor = Color.Black, // Border color when focused
                     cursorColor = Color.Black // Cursor color
                 ),
                 maxLines = 1, // Limits text field to one line
                 label = { Text(text = "Package Name", color = Color.Black) }, // Label text for the text field
                 placeholder = { Text(text = "Enter Package Name") }, // Placeholder text when the field is empty
             )

             // Button to submit the package name and add it to the blocked list
             Button(
                 onClick = {
                     // Action to execute when button is clicked
                     if (inputText.isNotEmpty()) { // Checks if input is not empty
                         // Adds the input text to the blocked apps list
                         blockedApps = blockedApps.toMutableList().apply { add(inputText.lowercase().trim()) } as ArrayList<String>
                         // Saves the updated list to preferences
                         preference.saveList("blockedAppsList", blockedApps)
                         // Clears the input field
                         inputText = ""
                     }
                 },
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(15.dp), // Adds padding around the button
                 colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton)) // Sets the button color
             ) {
                 // Button text
                 Text("Submit", color = Color.White) // Sets text color to white
             }
         }

         // LazyColumn to display the list of blocked apps
         LazyColumn(modifier = Modifier.fillMaxWidth()) {
             // Creates a list item for each blocked app
             items(blockedApps.size) { index ->
                 AppListItem(
                     app = blockedApps[index], // Passes the app name to the AppListItem composable
                     onDeleteClick = {
                         // Action to execute when delete button is clicked
                         blockedApps = blockedApps.toMutableList().apply {
                             remove(blockedApps[index]) // Removes the selected app from the list
                         } as ArrayList<String>
                         // Saves the updated list to preferences
                         preference.saveList("blockedAppsList", blockedApps)
                     }
                 )
                 Spacer(modifier = Modifier.width(8.dp)) // Adds spacing between list items
             }
         }
     }
 }

 @Composable
 fun AppListItem(app: String, onDeleteClick: () -> Unit) {
     // Retrieves the app icon for the given package name
     val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()

     Card(
         modifier = Modifier
             .fillMaxWidth()
             .background(Color(0xFF175AA8)) // Sets the card background color
             .padding(horizontal = 10.dp)
             .padding(top = 10.dp), // Adds padding around the card
         shape = Shape.large // Sets card shape to large
     ) {

         Row(
             modifier = Modifier
                 .fillMaxWidth()
                 .background(Color.White) // Sets the row background color to white
                 .padding(16.dp), // Adds padding inside the row
             verticalAlignment = Alignment.CenterVertically // Aligns items vertically in the center
         ) {
             // Box to display the app icon
             Box(
                 modifier = Modifier
                     .size(34.dp)
                     .clip(shape = Shape.medium)
                     .background(Color(0xFF175AA8)) // Sets the box background color
             ) {
                 imageBitmap?.let {
                     Image(
                         bitmap = it,
                         contentDescription = "App Icon", // Accessibility description for the image
                         modifier = Modifier.padding(8.dp) // Adds padding inside the box
                     )
                 }
             }
             Spacer(modifier = Modifier.width(16.dp)) // Adds spacing between the icon and text
             Row(
                 modifier = Modifier.fillMaxSize(),
                 verticalAlignment = Alignment.CenterVertically // Aligns text and delete icon vertically in the center
             ) {
                 // Text displaying the name of the blocked app
                 Text(
                     text = app,
                     color = Color.Black, // Sets text color to black
                     modifier = Modifier.weight(1f).padding(8.dp), // Adds padding and makes the text take available space
                 )
                 // Button to delete the app from the blocked list
                 IconButton(
                     onClick = onDeleteClick,
                     modifier = Modifier.size(30.dp).padding(end = 8.dp), // Sets size and padding for the button
                 ) {
                     Icon(
                         imageVector = Icons.Default.Delete, // Uses a default delete icon
                         contentDescription = "Delete", // Accessibility description for the icon
                         tint = Color.Black // Sets icon color to black
                     )
                 }
             }
         }
     }
 }


