package com.ifreeze.applock.presentation.activity

import android.content.Context
import android.text.TextUtils
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.data.cash.PreferencesGateway


/**
 * Composable function that represents the Login screen.
 * It displays input fields for username and password, and a login button.
 *
 * @param navController Navigation controller for handling navigation actions.
 * @param context Context for accessing resources and system services.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    navController: NavController,
    context: Context,
) {
    val preference = PreferencesGateway(LocalContext.current)
    // Apply Material theme with a white primary color
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color.White, // Set the primary color to white
        ),

        ) {
        // Main layout container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF175AA8)),

            ) {
            // Content column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,


                ) {
                // State for username and password fields
                val username = remember { mutableStateOf(TextFieldValue()) }
                val password = remember { mutableStateOf(TextFieldValue()) }
                var showPassword by remember { mutableStateOf(value = false) } // Toggle for password visibility
                // Header with back button
                BackArrow(onBackPressed = { navController.popBackStack() })
                // Login header logo
                HeaderLogo()
                // Spacing
                Spacer(modifier = Modifier.height(20.dp))
                // Username input field
                Box(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(12.dp),
                        label = { Text(text = "Username", color = Color.Gray) },
                        value = username.value,
                        onValueChange = {
                            username.value = it
                            preference.setPrefVal(context, "username22", it.text)
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black,
                            unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                            focusedBorderColor = Color.Black,
                            cursorColor = Color.Black
                        ),

                        maxLines = 1,
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        supportingText = { Text(text = "*Required", color = Color.Red) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        },

                        )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Password input field
                Box(
                    modifier = Modifier
                        .background(Color.White)
                ) {
                    OutlinedTextField(
                        modifier = Modifier.padding(12.dp),
                        label = {
                            Text(text = "Password", color = Color.Gray)
                        },
                        value = password.value,
                        visualTransformation = if (showPassword) {

                            VisualTransformation.None

                        } else {

                            PasswordVisualTransformation()

                        },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Password
                        ),

                        trailingIcon = {
                            if (showPassword) {
                                IconButton(onClick = { showPassword = false }) {
                                    Icon(
                                        imageVector = Icons.Filled.Visibility,
                                        contentDescription = "hide_password",
                                        tint = Color.Black
                                    )
                                }
                            } else {
                                IconButton(
                                    onClick = { showPassword = true }) {
                                    Icon(
                                        imageVector = Icons.Filled.VisibilityOff,
                                        contentDescription = "hide_password",
                                        tint = Color.Gray
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Black, // Text color // Color of the leading icon
                            unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                            focusedBorderColor = Color.Black,
                            cursorColor = Color.Black,
                        ),
                        maxLines = 1,
                        onValueChange = { password.value = it },
                        supportingText = { Text(text = "*Required", color = Color.Red) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Password,
                                contentDescription = null,
                                tint = Color.Black
                            )
                        },
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .padding(40.dp, 0.dp, 40.dp, 0.dp)
                        .background(Color(0xFF175AA8))
                ) {
                    Button(
                        onClick = {
                            // Validate input fields
                            if (TextUtils.isEmpty(username.value.text)) {
                                Toast.makeText(
                                    context,
                                    "Add Username",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            if (TextUtils.isEmpty(password.value.text)) {
                                Toast.makeText(
                                    context,
                                    "Add Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            // Check login credentials
                            if (username.value.text.isNotEmpty() && password.value.text.isNotEmpty()) {
                                if (username.value.text.trim() == "Admin" &&
                                    password.value.text.trim() == "HoNay@242"
                                ) {

                                    Toast.makeText(
                                        context,
                                        "Login Successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate(Screen.Home.route)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Incorrect Username or Password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 50.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))

                    ) {
                        Text(text = "Login", color = Color.White)

                    }
                }
            }
        }
    }
}

@Composable
fun BackArrow(onBackPressed: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
    }
}