package com.ifreeze.applock

import LightPrimaryColor
import SecondaryColor
import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.compse.ui.SetupNavGraph
import com.ifreeze.applock.presentation.activity.FullSystemScan
//import com.ifreeze.applock.presentation.activity.FullSystemScan
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.isLocationEnabled
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.service.AdminService
import com.ifreeze.applock.service.startAutoSyncWorker
import com.ifreeze.applock.ui.theme.AppLockTheme
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.di.NetWorkModule
import com.ifreeze.di.NetworkConfig
import com.patient.data.cashe.PreferencesGateway
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private lateinit var preferenc: PreferencesGateway
    lateinit var navController: NavHostController

    @Inject
    lateinit var networkConfig: NetworkConfig
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app.
        } else {

        }
    }


    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deviceManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, AdminService::class.java)
        preferenc = PreferencesGateway(applicationContext)
        val locationService = Intent(this, LOCATION_SERVICE::class.java)




        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                Log.d("abdo", "autoSync started")
                if (!isLocationEnabled(this)) {
                    startService(locationService)
                } else {
                    stopService(locationService)
                    startAutoSyncWorker(this)
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

            }
        }

        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_NETWORK_STATE
            )
            -> {

            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_NETWORK_STATE
                )
            }
        }


        setContent {
            window.statusBarColor = getColor(R.color.blue)
            AppLockTheme {
                navController = rememberNavController()
                SetupNavGraph(
                    navController = navController,
                    this, this, { wifiCheck() }, this, { webActivity() }, { systemScan() }
                )
            }
        }
    }

    fun CreateKiskoMode() {
        if (deviceManager.isDeviceOwnerApp(applicationContext.getPackageName())) {
            Log.d("islam", "CcreateKiskoMode : ${preferenc.load("kisko", false)}")
            if (preferenc.load("kisko", false) == false) {
                preferenc.update("kisko", true)
                startLockTask()
            } else {
                preferenc.update("kisko", true)
                stopLockTask()
            }
        }
    }

    fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(applicationContext)) {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(myIntent)
        }
    }

    private fun isValidUrl(url: String): Boolean {
        return try {
            val uri = URI(url)
            uri.isAbsolute && (uri.scheme == "http" || uri.scheme == "https")
        } catch (e: Exception) {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            1 -> {
                if (resultCode == RESULT_OK) {
                    checkOverlayPermission()
                    Log.d("islam", "onActivityResult:resultCode == RESULT_OK == true ")
                } else {
                    Log.d("islam", "onActivityResult:resultCode == RESULT_OK == false ")
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun permissions() {

    }

    fun systemScan() {
        this.startActivity(
            Intent(
                this,
                FullSystemScan::class.java
            )
        )
    }

    fun webActivity() {
        this.startActivity(
            Intent(
                this,
                MainWebActivity::class.java
            )
        )
    }

    fun wifiCheck() {
//        val broadcastNetworkReceiver = NetworkReceiver()
//        val intentFilterNetwork = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
//        registerReceiver(broadcastNetworkReceiver, intentFilterNetwork)
        Log.d("islam  ", "wifiCheck start: ${preferenc.load("WifiBlocked", false)} ")
//        val serviceIntent = Intent(this, NetworkMonitoringService::class.java)
//        if (preferenc.load("WifiBlocked", false) == true) {
//            startService(serviceIntent)
//        } else {
//            Log.d("islam", "wifiCheck if fals : ${preferenc.load("WifiBlocked", false)} ")
//            stopService(serviceIntent)
//        }
    }

}


@Composable
fun Demo_DropDownMenu(navController: NavController) {
    val preference = PreferencesGateway(LocalContext.current)
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var name = preference.getPrefVal(context).getString("username22", "Null").toString()
    val parts = name.split("@")
    name = parts[0]

    Row(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        horizontalArrangement = Arrangement.End

    ) {

        IconButton(
            onClick = { expanded = !expanded },
        ) {
            Image(
                modifier = Modifier.size(30.dp)
                    .background(Color.White, shape = Shape.large),

                painter = painterResource(id = R.drawable.person),

                contentDescription = "More",
                colorFilter = ColorFilter.tint(Color(0xFF175AA8))
            )


            DropdownMenu(

                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { }
                )
                DropdownMenuItem(
                    text = { Text("Logout") },
                    onClick = {
                        navController.navigate(Screen.AdminAccess.route)
                        Toast.makeText(context, "Logout Successfully", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


@Composable
fun HeaderLogo() {
    val logoImage = painterResource(id = R.drawable.ifreezelogo22)
    val fontAlger = FontFamily(Font(R.font.arial, FontWeight.Bold))

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

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
fun GeneralOptionsUI(
    navController: NavController,
    wifi: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)

    ) {

        GeneralSettingItem(
            icon = R.drawable.scan,
            mainText = "System Scan",
            subText = "Keep Your Mobile Secure and Initiate a Scan",
            onClick = {
                navController.navigate(Screen.Scan.route)

            }
        )

        GeneralSettingItem(
            icon = R.drawable.network_check,
            mainText = "Network Control",
            subText = "Administrate Your Wireless Network",
            onClick = {
                navController.navigate(Screen.NetworkControl.route)

                wifi()
            }
        )


        GeneralSettingItem(
            icon = R.drawable.web,
            mainText = "Web Filter",
            subText = "Manage Website Allowances",
            onClick = {
                navController.navigate(Screen.WebManager.route)
            }

        )

        GeneralSettingItem(
            icon = R.drawable.manage,
            mainText = "Application Manager",
            subText = "Manage Applications Permission",
            onClick = {
                navController.navigate(Screen.AppManager.route)
            }

        )

        GeneralSettingItem(
            icon = R.drawable.icon_settings,
            mainText = "Settings",
            subText = "Configure App Permissions",
            onClick = {
                navController.navigate(Screen.Setting.route)
            }
        )


    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingItem(icon: Int, mainText: String, subText: String, onClick: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        onClick = { onClick() },
        modifier = Modifier
            .padding(bottom = 12.dp)
            .fillMaxWidth()
    )
    {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 18.dp, horizontal = 14.dp)
                    .fillMaxWidth(),

                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = Shape.medium)
                        .background(Color(0xFF175AA8))
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(9.dp))
                Column(
                    modifier = Modifier.offset(y = (2).dp)
                ) {
                    Text(
                        text = mainText,
                        color = Color(0xFF175AA8),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )

                    Text(
                        text = subText,
                        color = Color(0xFF175AA8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,

                        )
                }

            }
        }
    }
}


@Composable
fun SupportOptionsUI(moveToApps: () -> Unit, kisko: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {
        Text(
            text = "Support",
            color = SecondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        SupportItem(
            icon = R.drawable.ic_baseline_keyboard_arrow_right_24,
            mainText = "Phone Applications ",
            onClick = {
                moveToApps()
            }
        )
        SupportItem(
            icon = R.drawable.ic_baseline_keyboard_arrow_right_24,
            mainText = "kiosk mode",
            onClick = {
                kisko()
            }
        )
        SupportItem(
            icon = R.drawable.ic_baseline_keyboard_arrow_right_24,
            mainText = "Privacy Policy",
            onClick = {}
        )
        SupportItem(
            icon = R.drawable.ic_baseline_keyboard_arrow_right_24,
            mainText = "About",
            onClick = {}
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportItem(icon: Int, mainText: String, onClick: () -> Unit) {
    Card(
        onClick = { onClick() },
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = Shape.medium)
                        .background(LightPrimaryColor)
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = "",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Text(
                    text = mainText,
                    color = SecondaryColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_keyboard_arrow_right_24),
                contentDescription = "",
                modifier = Modifier.size(16.dp)
            )

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppLockTheme {

    }
}



