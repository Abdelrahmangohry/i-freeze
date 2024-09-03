package com.ifreeze.applock

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
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
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ifreeze.applock.presentation.nav_graph.SetupNavGraph
import com.ifreeze.applock.presentation.AuthViewModel
import com.ifreeze.applock.presentation.activity.FullSystemScan
import com.ifreeze.applock.presentation.activity.MainWebActivity
import com.ifreeze.applock.presentation.activity.isNetworkAvailable
import com.ifreeze.applock.presentation.nav_graph.Screen
import com.ifreeze.applock.ui.theme.AppLockTheme
import com.ifreeze.applock.ui.theme.Shape
import com.ifreeze.data.cash.PreferencesGateway
import dagger.hilt.android.AndroidEntryPoint
import androidx.lifecycle.Observer
import com.ifreeze.applock.Receiver.MyDeviceAdminReceiver
import com.ifreeze.applock.ui.LoginActivityScreenSharing
import com.ifreeze.data.model.DeviceDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.Inet4Address
import java.net.NetworkInterface

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var deviceManager: DevicePolicyManager
    private lateinit var compName: ComponentName
    private lateinit var preference: PreferencesGateway
    lateinit var database: SQLiteDatabase
    lateinit var navController: NavHostController
    private val EXTERNAL_STORAGE_PERMISSION_CODE = 105

    // Inject AuthViewModel using Hilt
    private val authViewModel: AuthViewModel by viewModels()
    val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app.
        } else {

        }
    }


    @SuppressLint("HardwareIds", "Range")
    @RequiresApi(34)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        deviceManager = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        compName = ComponentName(this, MyDeviceAdminReceiver::class.java)
        preference = PreferencesGateway(applicationContext)

        var applicationNames = preference.getList("kioskApplications")
        var deviceId = preference.load("responseID", "")

        val enabledServicesSetting = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        //getting the device Name
        val deviceName: String = Build.BRAND + Build.MODEL
        //getting the operating system version
        val operatingSystemVersion: String = "Android " + Build.VERSION.RELEASE
        val ipAddress = getIpAddress()
        //getting the AndroidID
        val androidId: String =
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val hashesListDatabase = mutableListOf<String>()
        //    private lateinit var btn2: Button


        /**
         * Sets up the content view, initializes various components, and performs startup tasks.
         */
        setContent {
            window.statusBarColor = getColor(R.color.blue)
            AppLockTheme {
                navController = rememberNavController()
                SetupNavGraph(

                    navController = navController,
                    this,
                    this,
                    { wifiCheck() },
                    this,
                    { webActivity() },
                    { systemScan() },
                    preference,
                    { screenShareFun() }
                )

            }
        }




        GlobalScope.launch(Dispatchers.IO) {
            //Copy the database file from assets to internal storage
            copyDatabaseFileMain()
            // Open the database
            database = openOrCreateDatabase("scan.db", Context.MODE_PRIVATE, null)

            // Query the database and retrieve data from the specific table
            val cursor = database.rawQuery("SELECT * FROM Malware_hashs", null)

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val columnName = cursor.getString(cursor.getColumnIndex("sha256"))
                    if (columnName.isNullOrEmpty()) {
                        continue
                    } else {
                        hashesListDatabase.add(columnName)
                        // Process the retrieved data as needed
                    }
                }
                preference.saveList("hashesListDatabase", hashesListDatabase)
                cursor.close()

            }
        }


    }

    /**
     * Copies the database file from assets to internal storage if it doesn't already exist.
     */

    fun copyDatabaseFileMain() {
        try {
            val DATABASE_NAME = "scan.db"
            val inputStream: InputStream = assets.open(DATABASE_NAME)
            val outputFile = File(getDatabasePath(DATABASE_NAME).path)

            // Print the database path
            Log.d("DatabasePath", "Database path: ${outputFile.path}")

            if (!outputFile.exists()) {
                val outputStream = FileOutputStream(outputFile)
                val buffer = ByteArray(1024)
                var length: Int
                while (inputStream.read(buffer).also { length = it } > 0) {
                    outputStream.write(buffer, 0, length)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                Log.d("abdo", "Database copied successfully.")
            } else {
                Log.d("abdo", "Database already exists.")
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("DatabaseCopyError", "Cannot read database.")
        }
    }


    /**
     * Retrieves the IP address of the device.
     *
     * @return The IP address of the device or an empty string if not found.
     */
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

    /**
     * Checks if overlay permissions are granted, and if not, requests them.
     */
    fun checkOverlayPermission() {
        if (!Settings.canDrawOverlays(applicationContext)) {
            val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            applicationContext.startActivity(myIntent)
        }
    }


    /**
     * Handles the result of activity requests.
     *
     * @param requestCode The request code associated with the activity result.
     * @param resultCode The result code returned by the activity.
     * @param data The intent data returned by the activity.
     */
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

    /**
     * Starts the full system scan activity.
     */
    fun systemScan() {
        this.startActivity(
            Intent(
                this,
                FullSystemScan::class.java
            )
        )
    }

    /**
     * Starts the web activity.
     */
    fun webActivity() {
        this.startActivity(
            Intent(
                this,
                MainWebActivity::class.java
            )
        )
    }

    /**
     * Starts the screen sharing login activity.
     */
    fun screenShareFun() {
        this.startActivity(
            Intent(
                this,
                LoginActivityScreenSharing::class.java
            )
        )
    }

    fun wifiCheck() {
        Log.d("islam  ", "wifiCheck start: ${preference.load("WifiBlocked", false)} ")
    }

}

/**
 * Displays a dropdown menu in the UI with user options.
 *
 * @param navController The navigation controller for handling navigation actions.
 */
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


//header ui for logo and Freeze Your Risks text
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

/**
 * Composable function that displays a column of general settings options.
 *
 * @param navController The navigation controller used to navigate between screens.
 * @param wifi A lambda function to perform an action related to Wi-Fi.
 */
@Composable
fun GeneralOptionsUI(
    navController: NavController,
    wifi: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)

    ) {

        // Displays an item for initiating a system scan
        GeneralSettingItem(
            icon = R.drawable.scan,
            mainText = "System Scan",
            subText = "Initiate a scan to detect mobile threats",
            onClick = {
                navController.navigate(Screen.Scan.route)

            }
        )
        // Displays an item for managing network connections
        GeneralSettingItem(
            icon = R.drawable.network_check,
            mainText = "Network Control",
            subText = "Manage mobile network connections",
            onClick = {
                navController.navigate(Screen.NetworkControl.route)

                wifi()
            }
        )

        // Displays an item for managing web filters
        GeneralSettingItem(
            icon = R.drawable.web,
            mainText = "Web Filter",
            subText = "Set a policy for accessing websites",
            onClick = {
                navController.navigate(Screen.WebManager.route)
            }
        )
        // Displays an item for managing applications
        GeneralSettingItem(
            icon = R.drawable.manage,
            mainText = "App Manager",
            subText = "Select permitted applications on mobile",
            onClick = {
                navController.navigate(Screen.AppManager.route)
            }
        )
        // Displays an item for accessing app settings
        GeneralSettingItem(
            icon = R.drawable.icon_settings,
            mainText = "Settings",
            subText = "Modify i-Freeze settings and permissions",
            onClick = {
                navController.navigate(Screen.SettingAdmin.route)
            }
        )
    }
}


/**
 * Composable function that displays a general setting item within a card.
 *
 * @param icon The resource ID of the icon to display.
 * @param mainText The main text to display on the item.
 * @param subText The secondary text to display on the item.
 * @param onClick A lambda function to handle item click events.
 */
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


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppLockTheme {

    }
}



