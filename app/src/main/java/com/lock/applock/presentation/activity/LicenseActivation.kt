package com.lock.applock.presentation.activity


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
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
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.lock.applock.R
import com.lock.applock.presentation.AuthViewModel
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.applock.service.NetworkMonitoringService
import com.lock.data.model.DeviceDTO
import com.patient.data.cashe.PreferencesGateway
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

import java.net.Inet4Address
import java.net.NetworkInterface


@RequiresApi(34)
@Composable
fun LicenseActivation(
    navController: NavController, lifecycle: LifecycleOwner, context: Context,
    authViewModel: AuthViewModel = hiltViewModel()


) {

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF175AA8))
    ) {
        headerLicense(onBackPressed = { navController.popBackStack() })
        licenseKey(lifecycle, context, navController, authViewModel)

    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun licenseKey(
    lifecycle: LifecycleOwner,
    context: Context,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val preference = PreferencesGateway(LocalContext.current)
    var deviceId = remember { preference.load("responseID", "") }
    val serviceIntent = Intent(LocalContext.current, NetworkMonitoringService::class.java)
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
        Column(modifier = Modifier.wrapContentSize(align = Alignment.Center)) {

            ElevatedButton(
                onClick = {
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    }
                    authViewModel.getUserLogin(text, deviceDto)
                if (text.isEmpty()){
                    Toast.makeText(
                        context,
                        "Add License Key",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@ElevatedButton
                }else{
                    authViewModel._loginFlow.observe(lifecycle, Observer { response ->
                        if (response.isSuccessful) {
                            Log.d("abdo", response.body().toString())
//                            OutlinedTextField.visability.gone
                            deviceId = response.body().toString().trim()
                            Log.d("abdo", "this is new response ${deviceId!!}")

                            preference.save("responseID", deviceId!!)
                            Toast.makeText(
                                context,
                                "License Activate Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                            navController.navigate(Screen.AdminAccess.route)
                            return@Observer
                        } else {
                            Toast.makeText(
                                context,
                                "Invalid License Activate Key",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                    })}
                }, modifier = Modifier.padding(vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Activate", fontSize = 16.sp, color = Color.White)
            }

            ElevatedButton(
                onClick = {
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    }
                    if (deviceId.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            "Enter a Valid Key First",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        authViewModel.newUpdateUserData(deviceId!!)
                        authViewModel._newFlow.observe(lifecycle, Observer { responseId ->
                            if (responseId.isSuccessful) {
                                Log.d("abdo", "new response query ${responseId.body()?.data}")
                                preference.update(
                                    "Blacklist",
                                    responseId.body()?.data?.blockListApps!!
                                )
                                preference.update(
                                    "Whitelist",
                                    responseId.body()?.data?.whiteListApps!!
                                )
                                preference.update(
                                    "Browsers",
                                    responseId.body()?.data?.browsers!!
                                )
                                preference.update(
                                    "WebBlacklist",
                                    responseId.body()?.data?.blockListURLs!!
                                )
                                preference.update(
                                    "WebWhitelist",
                                    responseId.body()?.data?.whiteListURLs!!
                                )
                                preference.update(
                                    "WifiBlocked",
                                    responseId.body()?.data?.blockWiFi!!
                                )

                                if(responseId.body()?.data?.blockWiFi!!){
                                    context.startService(serviceIntent)
                                }
                                else{
                                    context.stopService(serviceIntent)
                                }

                                preference.update(
                                    "WifiWhite",
                                    responseId.body()?.data?.whiteListWiFi!!
                                )
                                Toast.makeText(
                                    context,
                                    "Data Synchronized Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.d("abdo", "kolo error ${responseId.message()}")
                            }
                        })
                    }
                }, modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("SYNC", fontSize = 16.sp, color = Color.White)
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





