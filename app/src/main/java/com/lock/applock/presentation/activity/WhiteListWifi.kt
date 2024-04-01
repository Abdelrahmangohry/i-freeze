package com.lock.applock.presentation.activity

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.TextFieldColors
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
import com.lock.applock.R
import com.lock.applock.ui.theme.Shape

import com.patient.data.cashe.PreferencesGateway

@Composable
fun WhiteListWifi(navController: NavController) {
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
            wifiHeaderMenu(onBackPressed = { navController.popBackStack() })
            var allowedWifi by remember {
                mutableStateOf(
                    preference.getList("allowedWifiList") ?: mutableListOf()
                )
            }

            SimpleInputTextWithButtonWifi(
                onValidMacSubmit = { value ->
                    allowedWifi =
                        allowedWifi.toMutableList().apply { add(value.lowercase().trim()) }
                    preference.saveList("allowedWifiList", allowedWifi)
                },
                allowedWifi = allowedWifi // Pass allowedWifi here
            )

            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(allowedWifi) { wifi ->
                    listItemWifi(

                        wifi = WifiData(name = wifi),
                        onDeleteClick = {
                            // Handle delete action here
                            allowedWifi = allowedWifi.filterNot { it == wifi }.toMutableList()
                            preference.saveList("allowedWifiList", allowedWifi)
                        }
                    )
                }
            }

        }
    }
}

data class WifiData(val name: String, val icon: String? = null)

@Composable
fun listItemWifi(wifi: WifiData, onDeleteClick: () -> Unit) {
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
                text = wifi.name,
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
fun SimpleInputTextWithButtonWifi(onValidMacSubmit: (String) -> Unit, allowedWifi: List<String>) {
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
            label = { Text(text = "Wifi", color = Color.Black) },
            placeholder = { Text(text = "Enter Wifi Name", color = Color.Gray) },

            )

        Button(
            onClick = {
                val wifiName = text2.value.trim()
                if (wifiName.isNotEmpty() && wifiName !in allowedWifi) {
                    onValidMacSubmit(wifiName)
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
fun wifiHeaderMenu(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Whitelisted WiFi",
            color = Color.White,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 30.dp).padding(horizontal = 55.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}

