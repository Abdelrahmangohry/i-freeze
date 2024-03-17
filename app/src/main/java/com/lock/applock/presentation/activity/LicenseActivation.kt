package com.lock.applock.presentation.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

@SuppressLint("FlowOperatorInvokedInComposition", "HardwareIds")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun licenseKey(
    lifecycle: LifecycleOwner,
    context: Context,
    navController: NavController,
    authViewModel: AuthViewModel
) {

    val preference = PreferencesGateway(LocalContext.current)
    var deviceId = preference.load("responseID", "")
    val syncTime = preference.load("time", "")
    val serviceIntent = Intent(context, NetworkMonitoringService::class.java)
//    var isVisible by remember { mutableStateOf(preference.load("IsVisible", true)) }
//    var showActivationBox = remember { mutableStateOf(preference.load("BoxShowed", true)) }

    var text by remember { mutableStateOf("") }


    //getting the device Name
    val deviceName: String = Build.BRAND + Build.MODEL
    //getting the operating system version
    val operatingSystemVersion: String = "Android " + Build.VERSION.RELEASE
    //getting the AndroidID
    val androidId: String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)

    //getting the IP Adress
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

    val ipAddress = getIpAddress()

    val deviceDto = DeviceDTO(
        deviceName = deviceName,
        operatingSystemVersion = operatingSystemVersion,
        deviceIp = ipAddress,
        macAddress = androidId,
        serialNumber = androidId
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

        Box(modifier = Modifier.background(color = Color.White)) {
            if (deviceId.isNullOrEmpty()) {
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
            } else {
                Text(
                    text = "License Activated Successfully",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center).background(Color(0xFF175AA8))
                )
            }

        }
        Column(modifier = Modifier.wrapContentSize(align = Alignment.Center)) {

            ElevatedButton(
                onClick = {
                    var locationPermissionRequestCode = 123
                    if (!deviceId.isNullOrEmpty()) {
                        Toast.makeText(
                            context,
                            "Licence Already Activated",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    }
                    if (!isNetworkAvailable(context)) {
                        Toast.makeText(
                            context,
                            "Please connect to the internet",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    }

                    if (!isLocationPermissionGranted(context)) {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            locationPermissionRequestCode
                        )

                    }
                    if (!isLocationEnabled(context)) {

                            context.startService(serviceIntent)
                    }
//                    else {
//                            context.stopService(serviceIntent)
//                        }
//                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

//// location permission check
                    if (text.isEmpty()) {
                        Toast.makeText(
                            context,
                            "Add License Key",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@ElevatedButton
                    } else {
                        authViewModel.getUserLogin(text, deviceDto)
                        authViewModel._loginFlow.observe(lifecycle, Observer { response ->
                            if (response.isSuccessful) {
                                Log.d("abdo", response.body().toString())
                                deviceId = response.body().toString().trim()

                                preference.save("responseID", deviceId!!)
                                Toast.makeText(
                                    context,
                                    "License Activate Successfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                                preference.save("IsVisible", false)
                                preference.save("BoxShowed", false)

                                navController.navigate(Screen.AdminAccess.route)
                                authViewModel.newUpdateUserData(deviceId!!)
                                authViewModel._newFlow.observe(lifecycle, Observer { responseId ->
                                    if (responseId.isSuccessful) {

                                        Log.d(
                                            "abdo",
                                            "response body from license ${responseId.body()}"
                                        )
                                        preference.update(
                                            "Blacklist",
                                            responseId.body()?.data?.device?.blockListApps!!
                                        )
                                        preference.update(
                                            "Whitelist",
                                            responseId.body()?.data?.device?.whiteListApps!!
                                        )
                                        preference.update(
                                            "Browsers",
                                            responseId.body()?.data?.device?.browsers!!
                                        )
                                        preference.update(
                                            "WebBlacklist",
                                            responseId.body()?.data?.device?.blockListURLs!!
                                        )
                                        preference.update(
                                            "WebWhitelist",
                                            responseId.body()?.data?.device?.whiteListURLs!!
                                        )
                                        preference.update(
                                            "WifiBlocked",
                                            responseId.body()?.data?.device?.blockWiFi!!
                                        )

                                        if (responseId.body()?.data?.device?.blockWiFi!!) {
                                            context.startService(serviceIntent)
                                        } else {
                                            context.stopService(serviceIntent)
                                        }
                                        preference.update(
                                            "WifiWhite",
                                            responseId.body()?.data?.device?.whiteListWiFi!!
                                        )
                                        preference.save(
                                            "time",
                                            "${responseId.body()?.data?.device?.time}"
                                        )
                                        Toast.makeText(
                                            context,
                                            "Data Synchronized Successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                })
                            } else {
                                Toast.makeText(
                                    context,
                                    context.resources.getString(R.string.invalid_license_activate_key),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Observer
                            }

                        })
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
            ) {
                Text("Activate", fontSize = 16.sp, color = Color.White)
            }


//            ElevatedButton(
//                onClick = {
//                    if (showActivationBox.value == false) {
//                        preference.save("IsVisible", false)
//                        showActivationBox.value = true
//                    } else {
//                        Toast.makeText(
//                            context,
//                            "Box Already Showed",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                },
//                modifier = Modifier.padding(vertical = 16.dp).align(Alignment.CenterHorizontally),
//                colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
//            ) {
//                Text("Show Activation Box", fontSize = 16.sp, color = Color.White)
//
//            }
            Text(
                "Last Update : $syncTime",
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

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

 fun isLocationPermissionGranted(context: Context): Boolean {
    return ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

 fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
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





