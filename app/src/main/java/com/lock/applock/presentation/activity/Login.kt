package com.lock.applock.presentation.activity

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.app.data.remote.NetWorkState

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.lock.applock.R
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.AuthViewModel
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.data.model.DeviceDTO
import com.patient.data.cashe.PreferencesGateway
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Login(
    navController: NavController,
    context: Context,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var auth: FirebaseAuth = Firebase.auth
    val preference = PreferencesGateway(LocalContext.current)
    viewModel.getUserLogin(
        "67E62E91-E758-42BA-C508-08DB4BB9CDC7",

        DeviceDTO(
            "192.168.1.1",
            "HESHAM",
            "005056C00001",
            "Microsoft windows 11 pro"
        )
    )
    Log.d("islam", "Login: ${viewModel.loginFlow.collectAsState(initial = NetWorkState.Loading)}")
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(
            primary = Color.White, // Set the primary color to white
        ),

        ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF175AA8)),

            ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,


                ) {
                val username = remember { mutableStateOf(TextFieldValue()) }
                val password = remember { mutableStateOf(TextFieldValue()) }
                var showPassword by remember { mutableStateOf(value = false) }

                HeaderLogin(onBackPressed = { navController.popBackStack() })

                loginHeaderLogo()

                Spacer(modifier = Modifier.height(20.dp))

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
                            if (!isOnline(context)) {
                                return@Button
                            }
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

                            if (username.value.text.isNotEmpty() && password.value.text.isNotEmpty()) {
                                auth.signInWithEmailAndPassword(
                                    username.value.text.toString().trim(),
                                    password.value.text.toString().trim()
                                ).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Log.d("islam", "Login :${task.isSuccessful} ")
                                        navController.navigate(Screen.Home.route)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            task.exception?.localizedMessage,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 50.dp),
                        colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))

                    ) {
                        Text(text = "Login", color = Color.White)

                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                ClickableText(
                    text = AnnotatedString("Forgot password?"),
                    onClick = { },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = Color.White,
                        textDecoration = TextDecoration.None // To remove underline
                    )

                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF175AA8)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ClickableText(
                        text = AnnotatedString("Sign up here"),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(20.dp),
                        onClick = { },
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            textDecoration = TextDecoration.None // To remove underline
                        )

                    )
                }
            }
        }
    }
}

fun isOnline(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities =
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    if (capabilities != null) {


        when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {

                Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {


                Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                return true
            }

            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {

                Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                return true
            }
        }
    } else {

        Toast.makeText(context, "Check connection to the internet", Toast.LENGTH_LONG)
            .show()
    }
    return false

}

@Composable
fun loginHeaderLogo() {
    val logoImage = painterResource(id = R.drawable.ifreezelogo22)
    val fontAlger = FontFamily(Font(R.font.alger, FontWeight.Light))

    Column(
        modifier = Modifier.fillMaxWidth()

    ) {
        Box(modifier = Modifier.fillMaxWidth())
        {

            Image(

                painter = logoImage,
                contentDescription = "iFreeze Logo",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(200.dp)
                    .align(Alignment.Center)
            )

            Text(
                text = "Freeze Your Risks",
                fontFamily = fontAlger,
                color = Color.White,
                modifier = Modifier
                    .padding(top = 90.dp, start = 10.dp)
                    .align(Alignment.Center),
                fontSize = 19.sp

            )

        }

    }


}

@Composable
fun HeaderLogin(onBackPressed: () -> Unit) {
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