package com.ifreeze.applock.presentation.activity

import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.AuthViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.data.model.TicketMessageBody
import com.patient.data.cashe.PreferencesGateway

@RequiresApi(34)
@Composable
fun SupportTeam(
    navController: NavController
) {

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        HeaderSupport(onBackPressed = { navController.popBackStack() })
        ticketBody(navController)

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ticketBody( navController: NavController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val context = LocalContext.current
    val preference = PreferencesGateway(context)
    val deviceId = preference.load("responseID", "")

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
                label = { Text("Name", style = TextStyle(color = Color.White))},
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Person, contentDescription = "Icon")
                },
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                    containerColor = Color.DarkGray,
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", style = TextStyle(color = Color.White)) },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Email, contentDescription = "Email")
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                    containerColor = Color.DarkGray,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone", style = TextStyle(color = Color.White)) },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                    containerColor = Color.DarkGray,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Phone, contentDescription = "Phone")
                },
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", style = TextStyle(color = Color.White)) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.White, // Change text color
                    cursorColor = Color.White, // Change cursor color
                    focusedIndicatorColor = Color.White, // Change focused indicator color
                    unfocusedIndicatorColor = Color.Black, // Change unfocused indicator color
                    containerColor = Color.DarkGray,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val message = TicketMessageBody(
                        deviceId = deviceId!!,
                        name = name.trim(),
                        email = email.trim(),
                        phone = phone.trim(),
                        description = description.trim(),
                    )
                    Log.d("abdo", "deviceId ${deviceId}")
                    Log.d("abdo", "name:  ${name.trim()}")
                    Log.d("abdo", "email:  ${email.trim()}")
                    Log.d("abdo", "phone:  ${phone.trim()}")
                    Log.d("abdo", "description:  ${description.trim()}")
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Please connect to the management server",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else if(name.trim().isNullOrEmpty()){
                        Toast.makeText(
                            context,
                            "Please add your name",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else if(email.trim().isNullOrEmpty()){
                        Toast.makeText(
                            context,
                            "Please add your e-mail",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else if(phone.trim().isNullOrEmpty()){
                        Toast.makeText(
                            context,
                            "Please add your phone",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else if(description.trim().isNullOrEmpty()){
                        Toast.makeText(
                            context,
                            "Please add a description",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }
                    else {
                        authViewModel.sendTicket(message)
                        Toast.makeText(
                            context,
                            "We Received Your Ticket Successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(Screen.AdminAccess.route)
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


@Composable
fun HeaderSupport(onBackPressed: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
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
                .padding(top = 8.dp, bottom = 30.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

