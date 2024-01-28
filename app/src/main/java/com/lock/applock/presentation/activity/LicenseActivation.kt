package com.lock.applock.presentation.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.constraintlayout.core.motion.utils.Utils
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.app.data.remote.NetWorkState
import com.lock.applock.R
import com.lock.applock.presentation.AuthViewModel
import com.lock.data.model.DeviceDTO
import com.patient.data.cashe.PreferencesGateway

import kotlinx.coroutines.flow.map
import java.net.Inet4Address
import java.net.NetworkInterface


@RequiresApi(34)
@Composable
fun LicenseActivation(
    navController: NavController, lifecycle: LifecycleOwner,
    authViewModel: AuthViewModel = hiltViewModel()


) {

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        headerLicense(onBackPressed = { navController.popBackStack() })
        licenseKey(lifecycle, authViewModel)

    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun licenseKey(lifecycle: LifecycleOwner, authViewModel: AuthViewModel) {

    val preference = PreferencesGateway(LocalContext.current)
    var responseID = remember { preference.load("responseID", "") }
//    val loginResponse by authViewModel.loginFlow.collectAsState(initial = null)
//    val updateResponse by authViewModel.updateFlow.collectAsState(initial = null)

    //getting the device Name
    val deviceName: String = Build.BRAND + Build.MODEL + "New"
    //getting the operating system version
    val operatingSystemVersion: String = "Android 1" + Build.VERSION.RELEASE
    val model: String = Build.MODEL
//    val brand: String = Build.BRAND
    val device: String = Build.DEVICE


    fun getIpAddress(): String {

        var ipAddress = ""
        try {
            val networkInterfaces = NetworkInterface.getNetworkInterfaces()
            while (networkInterfaces.hasMoreElements()) {
                val networkInterface = networkInterfaces.nextElement()
                val inetAddresses = networkInterface.inetAddresses
                while (inetAddresses.hasMoreElements()) {
                    val inetAddress = inetAddresses.nextElement()
                    if (inetAddress is Inet4Address && !inetAddress.isLoopbackAddress) {
                        ipAddress = inetAddress.hostAddress
                        break
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ipAddress
    }


//        fun getMacAddress(): String {
//        val wifiManager = context.getSystemService(WIFI_SERVICE) as WifiManager
//        val wInfo: WifiInfo = wifiManager.connectionInfo
//        val macAddress: String = wInfo.macAddress
//
//        return macAddress
//    }

//    val macAddress = getMacAddress()
    val ipAddress = getIpAddress()

    var responseNew by remember { mutableStateOf<String?>(null) }
    var text by remember { mutableStateOf("") }
    val deviceDto = DeviceDTO(
        deviceName = deviceName,
        operatingSystemVersion = operatingSystemVersion,
        deviceIp = ipAddress,
        macAddress = "macAddress"
    )


    Row(
        modifier = Modifier.padding(top = 100.dp, bottom = 30.dp).fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "License Activation",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
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
                    authViewModel._loginFlow.observe(lifecycle, Observer { response ->
                        if (response.isSuccessful) {
                            Log.d("abdo", response.body().toString())

                            responseNew = response.body().toString().trim()
                            Log.d("abdo", "this is new response ${responseNew!!}")

                            preference.save("responseID", responseNew!!)

                            // Move the following code inside the Observer block
                            authViewModel.updateUserData(responseNew!!)
                            authViewModel._updateFlow.observe(lifecycle, Observer { it
                                if (it.isSuccessful) {
                                    Log.d("abdo", "all Responses body ${it.body()}")
                                    preference.update(
                                        "Blacklist",
                                        it.body()?.blockListApps!!

                                    )
                                    preference.update(
                                        "Whitelist",
                                        it.body()?.whiteListApps!!
                                    )
                                    preference.update(
                                        "Browsers",
                                        it.body()?.browsers!!
                                    )
                                    preference.update(
                                        "WebBlacklist",
                                        it.body()?.blockListURLs!!
                                    )
                                    preference.update(
                                        "WebWhitelist",
                                        it.body()?.whiteListURLs!!
                                    )
                                    preference.update(
                                        "WifiBlocked",
                                        it.body()?.blockWiFi!!
                                    )
                                    preference.update(
                                        "WifiWhite",
                                        it.body()?.whiteListWiFi!!
                                    )

                                } else {
                                    Log.d("abdo", "kolo error ${it.message()}")
                                }
                            })
                        } else {
                            Log.d("abdo", response.message())
                        }
                    })
                }, modifier = Modifier.padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Activate", fontSize = 16.sp, color = Color.White)
            }

//            ElevatedButton(
//                onClick = {


//                            authViewModel.updateUserData(responseID!!)
//                            authViewModel._updateFlow.observe(lifecycle, Observer { response2 ->
//                                if (response2.isSuccessful) {
//                                    Log.d("abdo", "all Responses body ${response2.body()}")
//                                    preference.update("Blacklist",response2.body()?.BlockListApps ?: true)
//                                    preference.update("Whitelist",response2.body()?.WhiteListApps ?: false)
//                                    preference.update("Browsers",response2.body()?.Browsers ?: false)
//                                    preference.update("WebBlacklist",response2.body()?.BlockListURLs ?: false)
//                                    preference.update("WebWhitelist",response2.body()?.WhiteListURLs ?: false)
//                                    preference.update("WifiBlocked",response2.body()?.BlockWiFi ?: false)
//                                    preference.update("WifiWhite",response2.body()?.WhiteListWiFi ?: false)
//
//                                } else {
//                                    Log.d("abdo", "kolo error ${response2.message()}")
//                                }
//                            })

//                }, modifier = Modifier.padding(vertical = 16.dp),
//                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
//            ) {
//                Text("SynC", fontSize = 16.sp, color = Color.White)
//            }
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





