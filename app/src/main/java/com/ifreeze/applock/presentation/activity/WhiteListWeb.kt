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
import com.patient.data.cashe.PreferencesGateway

@Composable
fun WhiteListWeb(navController: NavController) {
    val preference = PreferencesGateway(LocalContext.current)

    Column(
        modifier = Modifier
            .background(Color(0xFF175AA8))
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp),
        ) {
            HeaderMenu(onBackPressed = { navController.popBackStack() }, "Whitelisted Websites")
            var allowedWebsites by remember {
                mutableStateOf(
                    preference.getList("allowedWebsites") ?: mutableListOf()
                )
            }

            textWithButtonWhiteListWebView(onValidMacSubmit = { value ->
                allowedWebsites =
                    allowedWebsites.toMutableList().apply { add(value.lowercase().trim()) }
                preference.saveList("allowedWebsites", allowedWebsites)
            })

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allowedWebsites) { website ->
                    ListItemWhiteListWeb(
                        webSitesWhite = WebSiteWhiteList(name = website),
                        onDeleteClick = {
                            // Handle delete action here
                            allowedWebsites =
                                allowedWebsites.filterNot { it == website }.toMutableList()
                            preference.saveList("allowedWebsites", allowedWebsites)
                        }
                    )
                }
            }

//            Log.d("abd", "ssidList items: ${ssidList.joinToString()}")
        }
    }
}

data class WebSiteWhiteList(val name: String, val icon: String? = null)

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
            Text(
                text = webSitesWhite.name,
                modifier = Modifier.weight(1f).padding(8.dp),
                color = Color.Black
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun textWithButtonWhiteListWebView(onValidMacSubmit: (String) -> Unit) {
    val text2 = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = text2.value,
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
            maxLines = 1,
            onValueChange = { newText ->
                text2.value = newText
            },
            label = { Text(text = "Address", color = Color.Black) },
            placeholder = { Text(text = "Enter Address", color = Color.Black) },
        )

        Button(
            onClick = {
                val newWebsite = text2.value.trim()
                if (newWebsite.isNotEmpty()) {
                    // Only add to the list if not empty
                    onValidMacSubmit(newWebsite)
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


