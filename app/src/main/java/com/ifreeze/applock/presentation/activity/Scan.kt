package com.ifreeze.applock.presentation.activity

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.presentation.nav_graph.Screen


@Composable
fun Scan(navController: NavController, filesScan: () -> Unit) {
    // Main Column that holds the scan settings and scan files options
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF175AA8))
    ) {
        // Inner Column for padding and organizing settings and scan options
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
                .padding(top = 10.dp)
        ) {
            // Composable for the scan title with a back button
            HeaderMenu(
                onBackPressed = { navController.popBackStack() },
                "System Scan"
            )

            // General setting item for navigating to scan settings
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

















