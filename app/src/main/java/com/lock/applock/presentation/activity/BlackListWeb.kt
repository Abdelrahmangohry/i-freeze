package com.lock.applock.presentation.activity

import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.materialIcon
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lock.applock.R
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.applock.presentation.screen.SimpleInputTextWithButton
import com.lock.applock.presentation.screen.isMacAddress
import com.lock.applock.ui.theme.Shape
import com.patient.data.cashe.PreferencesGateway
import java.security.KeyStore.Entry

@RequiresApi(34)
@Composable
fun BlackListWeb(navController: NavController) {
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
            webBlackListTitle(onBackPressed = { navController.popBackStack() })
            var blockedWebsites by remember {
                mutableStateOf(
                    preference.getList("blockedWebsites") ?: mutableListOf()
                )
            }

            textWithButtonBlackListWebView(
                onValidMacSubmit = { value ->
                    blockedWebsites =
                        blockedWebsites.toMutableList().apply { add(value.lowercase().trim()) }
                    preference.saveList("blockedWebsites", blockedWebsites)
                },
                blockedWebsites = blockedWebsites
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(blockedWebsites) { website ->
                    ListItemBlackListWeb(
                        webSites = WebSiteBlack(name = website),
                        onDeleteClick = {
                            // Handle delete action here
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

data class WebSiteBlack(val name: String, val icon: String? = null)

@Composable
fun ListItemBlackListWeb(webSites: WebSiteBlack, onDeleteClick: () -> Unit) {
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
                text = webSites.name,
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
fun textWithButtonBlackListWebView(
    onValidMacSubmit: (String) -> Unit,
    blockedWebsites: List<String>
) {
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
            placeholder = { Text(text = "Enter Address") },
        )

        Button(
            onClick = {
                val newWebsite = text2.value.trim()
                if (newWebsite.isNotEmpty() && newWebsite !in blockedWebsites) {
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


@Composable
fun webBlackListTitle(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Blacklisted Websites",
            color = Color.White,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 30.dp).padding(horizontal = 35.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}
