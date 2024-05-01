package com.lock.applock.presentation.activity.ui.theme

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.lock.applock.R

@RequiresApi(34)
@Composable
fun SupportTeam(
    navController: NavController
) {

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        HeaderSupport(onBackPressed = { navController.popBackStack() })
        ticketBody()

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ticketBody() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF175AA8)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Please Enable Internet Connection",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {

                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Submit", color = Color.White)
            }
        }
    }
}


// Function to check if the device is connected to the internet
@SuppressLint("ServiceCast")
fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}


@Composable
fun HeaderSupport(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "Support",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 30.dp).padding(horizontal = 95.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}

