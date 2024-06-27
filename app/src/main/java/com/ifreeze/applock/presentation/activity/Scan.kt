package com.ifreeze.applock.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.nav_graph.Screen


@Composable
fun Scan(navController: NavController, filesScan: () -> Unit) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp)
        ) {
            scanTitle(onBackPressed = { navController.popBackStack() })

            GeneralSettingItem(
                icon = R.drawable.icon_settings,
                mainText = "Scan Settings",
                subText = "Check the configured settings on mobile",
                onClick = {
                    navController.navigate(Screen.ScanProperties.route)
                }

            )

            GeneralSettingItem(
                icon = R.drawable.folder,
                mainText = "Scan Files",
                subText = "Detect infected files in mobile storage",
                onClick = {
                    filesScan()
                }

            )
        }
    }
}

@Composable
fun scanTitle(onBackPressed: () -> Unit) {

    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Distribute space evenly

    ) {
        IconButton(onClick = { onBackPressed() }) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = null,
                tint = Color.White
            )
        }
        Text(
            text = "System Scan",
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, bottom = 30.dp).padding(horizontal = 60.dp),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp
        )
    }
}
















