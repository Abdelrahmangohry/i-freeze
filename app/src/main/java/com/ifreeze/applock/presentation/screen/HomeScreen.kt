package com.ifreeze.applock.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.ifreeze.applock.Demo_DropDownMenu
import com.ifreeze.applock.GeneralOptionsUI
import com.ifreeze.applock.HeaderLogo

/**
 * Composable function for the Home screen.
 *
 * This screen serves as the main view of the application, displaying various UI components
 * such as a dropdown menu, a header logo, and general options.
 *
 * @param navController The [NavController] used for navigating between screens.
 * @param wifi A lambda function to handle Wi-Fi-related actions.
 */
@Composable
fun HomeScreen(navController: NavController ,wifi: () -> Unit) {
    Column (
        modifier = Modifier.background(Color(0xFF175AA8))
            .fillMaxSize()
    ){

        // Displays a dropdown menu with navigation options.
        Demo_DropDownMenu(navController)

        // Displays the header logo at the top of the screen.
        HeaderLogo()

        // Displays general options available on the home screen.
        GeneralOptionsUI(navController, wifi)
    }
}
