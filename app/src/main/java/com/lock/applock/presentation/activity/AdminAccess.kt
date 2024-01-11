package com.lock.applock.presentation.activity

import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.lock.applock.R
import com.lock.applock.helper.getListApps
import com.lock.applock.presentation.AppsViewModel
import com.lock.applock.presentation.nav_graph.Screen
import com.lock.applock.ui.theme.Shape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.patient.data.cashe.PreferencesGateway
import java.lang.Math.sin

@Composable
fun AdminAccess(navController: NavController, viewModel: AppsViewModel = hiltViewModel()) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        HeaderLogo()
        GeneralOptionsUI(navController)
    }
}

@Composable
fun HeaderLogo() {
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
fun GeneralOptionsUI(navController: NavController) {

    Column(
        modifier = Modifier
            .padding(horizontal = 14.dp)
            .padding(top = 10.dp)
    ) {

        GeneralSettingItem(
            icon = R.drawable.scan,
            mainText = "System Scan",
            subText = "Keep Your Mobile Secure and Initiate a Scan",
            onClick = {

            }
        )

        GeneralSettingItem(
            icon = R.drawable.web,
            mainText = "Web Access",
            subText = "Roam within the Allowed Boundaries",
            onClick = {
                navController.navigate(Screen.GeneralWebView.route)
            }
        )

        GeneralSettingItem(
            icon = R.drawable.admin_panel,
            mainText = "Admin Login",
            subText = "Administrative Privileges",
            onClick = {
                navController.navigate(Screen.Home.route)

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

        GeneralSettingItem(
            icon = R.drawable.baseline_key_24,
            mainText = "License Activation",
            subText = "Activate Your License",
            onClick = {
                navController.navigate(Screen.LicenseActivation.route)

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
                    .padding(vertical = 10.dp, horizontal = 14.dp)
                    .fillMaxWidth(),


                ) {
                Row(
                    modifier = Modifier.padding(vertical = 8.dp),
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
}




