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
 import com.patient.data.cashe.PreferencesGateway


 @OptIn(ExperimentalMaterial3Api::class)
 @Composable
 fun BlackList(viewModel: AppsViewModel = hiltViewModel(), navController: NavController) {

     val preference = PreferencesGateway(LocalContext.current)
     var blockedApps by remember { mutableStateOf(preference.getList("blockedAppsList")) }

     val selectedApps = remember { mutableSetOf<String>() }
     var inputText by remember { mutableStateOf("") }

     Column(
         modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
     ) {
         Row(
             modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
         ) {
             IconButton(onClick = { navController.popBackStack() }) {
                 Icon(
                     imageVector = Icons.Default.ArrowBack,
                     contentDescription = null,
                     tint = Color.White
                 )
             }
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

         Column(
             modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
         ) {
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

             Button(
                 onClick = {
                     if (inputText.isNotEmpty()) {
                         blockedApps =
                             blockedApps.toMutableList().apply { add(inputText.lowercase().trim()) } as ArrayList<String>
                         preference.saveList("blockedAppsList", blockedApps)
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

         LazyColumn(modifier = Modifier.fillMaxWidth()) {
             items(blockedApps.size) { index ->
                 AppListItem(
                     app = blockedApps[index],
                     onDeleteClick = {
                         // Handle delete action here
                         blockedApps = blockedApps.toMutableList().apply {
                             remove(blockedApps[index])
                         } as ArrayList<String>
                         preference.saveList("blockedAppsList", blockedApps)
                     }
                 )
                 Spacer(modifier = Modifier.width(8.dp))
             }
         }
     }
 }

@Composable
fun AppListItem(app: String, onDeleteClick: () -> Unit) {
    val imageBitmap = LocalContext.current.getAppIconByPackageName(app)?.toImageBitmap()
    Card(
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
                    modifier = Modifier.weight(1f).padding(8.dp),
                )
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

