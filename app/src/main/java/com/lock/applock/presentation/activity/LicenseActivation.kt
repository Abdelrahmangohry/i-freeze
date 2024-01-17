package com.lock.applock.presentation.activity


import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.data.remote.NetWorkState
import com.lock.applock.R
import com.lock.applock.presentation.AuthViewModel
import com.lock.data.model.DeviceDTO
import kotlinx.coroutines.flow.map


@RequiresApi(34)
@Composable
fun LicenseActivation(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        headerLicense(onBackPressed = { navController.popBackStack() })
        licenseKey(authViewModel)

    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun licenseKey(authViewModel: AuthViewModel) {
    //getting the device Name
    val deviceName: String = Build.MODEL
    //getting the operating system version
    val operatingSystemVersion: String = "Android " + Build.VERSION.RELEASE
    val model: String = Build.MODEL
    val brand: String = Build.BRAND
    val device: String = Build.DEVICE

//    Log.d("abdo", "device name ${deviceName}")
//    Log.d("abdo", " operatingSystemVersion ${operatingSystemVersion}")
//    Log.d("abdo", " model ${model}")
//    Log.d("abdo", " brand ${brand}")
//    Log.d("abdo", " device ${device}")

    var text by remember { mutableStateOf("") }
    val deviceDto = DeviceDTO(
        deviceName = deviceName,
        operatingSystemVersion = operatingSystemVersion,
        deviceIp = "YourDeviceIp",
        macAddress = "YourMacAddress"
    )

    val loginState by authViewModel.loginFlow
        .map { it }
        .collectAsState(initial = NetWorkState.Loading)
    Row(modifier = Modifier.padding(top = 100.dp, bottom = 30.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "License Activation",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Column(modifier = Modifier.background(color = Color.White)) {


            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("xxxx-xxxx-xxxx-xxxx", color = Color.Black) },
                maxLines = 1,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Black, // Text color // Color of the leading icon
                    unfocusedBorderColor = Color.LightGray, // Border color when unfocused
                    focusedBorderColor = Color.Black,
                    cursorColor = Color.Black
                ),
                modifier = Modifier.padding(20.dp),
            )

        }
        Column {
            ElevatedButton(
                onClick = {
                    authViewModel.getUserLogin(text, deviceDto)


                }, modifier = Modifier.padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Activate", fontSize = 16.sp, color = Color.White)
            }
        }
        //Observe the loginFlow and show Toast messages accordingly
        when (loginState) {
            is NetWorkState.Success<*> -> {
                Toast.makeText(
                    LocalContext.current,
                    "Activation successful!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            is NetWorkState.Error -> {
                Toast.makeText(
                    LocalContext.current,
                    "Activation failed",
                    Toast.LENGTH_SHORT
                ).show()
            }

            else -> {}
        }
    }
}

@Composable
fun headerLicense(onBackPressed: () -> Unit) {
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





