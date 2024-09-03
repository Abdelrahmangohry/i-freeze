package com.ifreeze.applock.presentation.nav_graph

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.ifreeze.applock.presentation.activity.AdminAccess
import com.ifreeze.applock.presentation.activity.AppManager
import com.ifreeze.applock.presentation.activity.BlackList
import com.ifreeze.applock.presentation.activity.BlackListWeb
import com.ifreeze.applock.presentation.activity.KioskMode
import com.ifreeze.applock.presentation.activity.LicenseActivation
import com.ifreeze.applock.presentation.activity.Login
import com.ifreeze.applock.presentation.activity.Scan
import com.ifreeze.applock.presentation.activity.ScanProperties
import com.ifreeze.applock.presentation.activity.SettingAdmin
import com.ifreeze.applock.presentation.activity.SettingScreen
import com.ifreeze.applock.presentation.activity.WebManager
import com.ifreeze.applock.presentation.activity.WhiteList
import com.ifreeze.applock.presentation.activity.WhiteListWeb
import com.ifreeze.applock.presentation.activity.WhiteListWifi
import com.ifreeze.applock.presentation.activity.SupportTeam
import com.ifreeze.applock.boardingscreen.OnboardingScreen1
import com.ifreeze.applock.boardingscreen.OnboardingScreen2
import com.ifreeze.applock.boardingscreen.OnboardingScreen3
import com.ifreeze.applock.boardingscreen.OnboardingScreen4
import com.ifreeze.applock.presentation.screen.HomeScreen
import com.ifreeze.applock.presentation.screen.NetworkControl
import com.ifreeze.applock.presentation.screen.SplashScreen
import com.ifreeze.data.cash.PreferencesGateway

/**
 * Configures the navigation graph for the home section of the application.
 *
 * This function sets up the navigation routes and their corresponding composable destinations
 * within the home navigation graph, based on whether onboarding screens have been displayed
 * or not. It also takes in various parameters including context, activity, and functions for
 * different actions.
 *
 * @param navController The NavHostController used for navigating between screens.
 * @param activity The Activity context where navigation takes place.
 * @param context The context used for accessing application resources.
 * @param wifi A lambda function to handle WiFi-related actions.
 * @param lifecycle The LifecycleOwner used to manage the lifecycle of the navigation graph.
 * @param webStart A lambda function to initiate web-related actions.
 * @param fileScan A lambda function to handle file scanning actions.
 * @param preferences An instance of PreferencesGateway used for retrieving and saving preferences.
 * @param screenShareFun A lambda function to handle screen sharing actions.
 */

@RequiresApi(34)
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    activity : Activity, context:Context, wifi: () -> Unit,
    lifecycle: LifecycleOwner, webStart : () -> Unit, fileScan : () -> Unit, preferences: PreferencesGateway,
    screenShareFun: () -> Unit
) {
    // Load preference to determine if onboarding screens have been displayed
    val isDisplayed = preferences.load("isDisplayed", false)
    // Define the navigation graph with a start destination based on preference
    navigation(
        startDestination = if(isDisplayed == true) Screen.AdminAccess.route else Screen.OnboardingScreen1.route,
        route = HOME_GRAPH_ROUTE
    ) {
        // Onboarding Screens
        composable(route = Screen.OnboardingScreen1.route) {
            OnboardingScreen1(navController = navController)
        }
        composable(route = Screen.OnboardingScreen2.route) {
            OnboardingScreen2(navController = navController)
        }
        composable(route = Screen.OnboardingScreen3.route) {
            OnboardingScreen3(navController = navController)
        }
        composable(route = Screen.OnboardingScreen4.route) {
            OnboardingScreen4(navController = navController)
            // Save preference indicating onboarding has been displayed
            preferences.save("isDisplayed", true)

        }
        // Admin Access Screen
        composable(
            route = Screen.AdminAccess.route
        ) {
            AdminAccess(navController = navController, webStart, screenShareFun)
        }
        // Login Screen
        composable(
            route = Screen.Login.route
        ) {
            Login(navController = navController,context)
        }
        // Home Screen
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navController, wifi)
        }
        // KioskMode Screen
        composable(
            route = Screen.KioskMode.route
        ) {
            KioskMode()
        }

        // SplashScreen Screen
        composable(
            route = Screen.Splash.route
        ) {
            SplashScreen(navController = navController,preferences)
        }

        // BlackList Screen
        composable(
            route = Screen.BlackList.route
        ) {
            BlackList(navController = navController)
        }
        // BlackListWeb Screen
        composable(
            route = Screen.BlackListWeb.route
        ) {
            BlackListWeb(navController = navController)
        }
        // WhiteListWeb Screen
        composable(
            route = Screen.WhiteListWeb.route
        ) {
            WhiteListWeb(navController = navController)
        }

        // WhiteListWifi Screen
        composable(
            route = Screen.WhiteListWifi.route
        ) {
            WhiteListWifi(navController = navController)
        }
        // WhiteList Screen
        composable(
            route = Screen.WhiteList.route
        ) {
            WhiteList(navController = navController)
        }
        composable(
            route = Screen.Setting.route
        ) {
            SettingScreen(navController = navController,activity)
        }

        composable(
            route = Screen.SettingAdmin.route
        ) {
            SettingAdmin(navController = navController)
        }

        composable(
            route = Screen.SupportTeam.route
        ) {
            SupportTeam(navController = navController)
        }

        composable(
            route = Screen.LicenseActivation.route
        ) {
            LicenseActivation(navController = navController, lifecycle,context)
        }
        composable(
            route = Screen.AppManager.route
        ) {
            AppManager(navController = navController)
        }

        composable(
            route = Screen.WebManager.route
        ) {
            WebManager(navController = navController)
        }

        composable(
            route = Screen.NetworkControl.route
        ) {
            NetworkControl(navController = navController,wifi)
        }

        composable(
            route = Screen.Scan.route
        ) {
            Scan(navController = navController, fileScan)
        }

        composable(
            route = Screen.ScanProperties.route
        ) {
            ScanProperties(navController = navController, lifecycle)
        }


    }
}