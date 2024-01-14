package com.lock.applock.presentation.activity


import android.annotation.SuppressLint
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
fun LicenseActivation(navController: NavController, authViewModel: AuthViewModel= hiltViewModel()) {
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
fun licenseKey(authViewModel:AuthViewModel) {
    var text by remember { mutableStateOf("") }
    val deviceDto = DeviceDTO(
        deviceName = "YourDeviceName",
        operatingSystemVersion = "YourOperatingSystemVersion",
        deviceIp = "YourDeviceIp",
        macAddress = "YourMacAddress")

    val loginState by authViewModel.loginFlow
        .map { it }
        .collectAsState(initial = NetWorkState.Loading)

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.background(color = Color.White)) {


            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("xxxx-xxxx-xxxx-xxxx") },
                maxLines = 1,


                textStyle = TextStyle(color = Color.Blue, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(20.dp),
                )

        }
        Column {
            ElevatedButton(onClick = {
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
}}

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
        Text(
            text = "License Activation",
            color = Color.White,

            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp).padding(horizontal = 70.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp
        )
    }
}





