package com.lock.applock.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.lock.applock.Demo_DropDownMenu
import com.lock.applock.GeneralOptionsUI
import com.lock.applock.HeaderLogo

@Composable
fun HomeScreen(navController: NavController ,wifi: () -> Unit) {
    Column (
        modifier = Modifier.background(Color(0xFF175AA8))
            .fillMaxSize()
    ){

        Demo_DropDownMenu(navController)
        HeaderLogo()

        GeneralOptionsUI(navController,wifi)
    }
}
