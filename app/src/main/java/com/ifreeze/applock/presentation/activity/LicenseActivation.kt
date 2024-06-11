package com.ifreeze.applock.presentation.activity


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.AuthViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.data.model.DeviceDTO
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
    var text by remember { mutableStateOf("") }

    //getting the device Name
    val deviceName: String = Build.BRAND + Build.MODEL
    //getting the operating system version
    val operatingSystemVersion: String = "Android " + Build.VERSION.RELEASE
    //getting the AndroidID
    val androidId: String =
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    val enabledServicesSetting = Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )

    //getting the IP Address
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
    Log.d("abdo", "ipAddress $ipAddress")

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
                Text(
                    text = "Please activate license key",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center).background(Color(0xFF175AA8))
                )
            } else {
                Text(
                    text = "License is activated",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center).background(Color(0xFF175AA8))
                )
            }

        }
        Column(modifier = Modifier.wrapContentSize(align = Alignment.Center)) {
            if (deviceId.isNullOrEmpty()) {

                ElevatedButton(
                    onClick = {
                        val newBaseUrl = "http://192.168.1.250:8443/api/"

                        preference.saveBaseUrl(newBaseUrl)
                        Log.d("abdo", "newBaseUrl $newBaseUrl")
                        if (!isNetworkAvailable(context)) {
                            Toast.makeText(
                                context,
                                "Please connect to the management server",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ElevatedButton
                        }

                            authViewModel.getUserLogin(deviceDto)
                            authViewModel._loginFlow.observe(lifecycle, Observer { response ->
                                if (response.isSuccessful) {
                                    Log.d("abdo", response.body().toString())
                                    deviceId = response.body().toString().trim()

                                    preference.save("responseID", deviceId!!)
                                    Toast.makeText(
                                        context,
                                        "License Activated",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    if (
                                        !Settings.canDrawOverlays(context) ||
                                        enabledServicesSetting?.contains("com.ifreeze.applock.service.AccessibilityServices") != true ||
                                        ContextCompat.checkSelfPermission(
                                            context,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "Please enable i-Freeze permissions in app settings",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    authViewModel.getCloudURL(deviceId!!)
                                    authViewModel._getcloudURL.observe(lifecycle, Observer{
                                        response ->
                                        if (response.isSuccessful){

                                        }
                                    })

                                    preference.save("IsVisible", false)
                                    preference.save("BoxShowed", false)
                                    navController.navigate(Screen.AdminAccess.route)

                                    authViewModel.newUpdateUserData(deviceId!!)
                                    authViewModel._newFlow.observe(
                                        lifecycle,
                                        Observer { responseId ->
                                            if (responseId.isSuccessful) {

                                                val licenseID =
                                                    responseId.body()?.data?.device?.licenseId
                                                Log.d(
                                                    "abdo",
                                                    "response body from license ${responseId.body()?.data?.device?.licenseId}"
                                                )
                                                preference.save("licenseID", licenseID!!)
                                                preference.save(
                                                    "time",
                                                    "${responseId.body()?.data?.device?.time}"
                                                )
                                                val isLicenseValid = true
                                                preference.save("validLicense", isLicenseValid)

                                            }
                                        })
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Invalid License Activation Key",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Observer
                                }

                            })

                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(colorResource(R.color.grayButton))
                ) {
                    Text("Activate", fontSize = 16.sp, color = Color.White)
                }
            }

            if (syncTime.isNullOrEmpty()) {
                Text(
                    "",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp)
                )

            } else {
                Text(
                    "Last Update : $syncTime",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 15.dp)
                )
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





