package com.ifreeze.applock.presentation.nav_graph

import android.app.Activity
import android.content.Context
import android.gesture.GestureOverlayView
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.compse.ui.*
import com.ifreeze.applock.presentation.activity.AdminAccess
import com.ifreeze.applock.presentation.activity.AllBrowsersList
import com.ifreeze.applock.presentation.activity.AppManager
import com.ifreeze.applock.presentation.activity.BlackList
import com.ifreeze.applock.presentation.activity.BlackListWeb
import com.ifreeze.applock.presentation.activity.GeneralWebView
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
import com.ifreeze.applock.presentation.screen.HomeScreen
import com.ifreeze.applock.presentation.screen.NetworkControl
import com.ifreeze.applock.presentation.screen.SplashScreen

@RequiresApi(34)
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    activity : Activity, context:Context, wifi: () -> Unit,
    lifecycle: LifecycleOwner, webStart : () -> Unit, fileScan : () -> Unit
) {
    navigation(
        startDestination = Screen.AdminAccess.route,
        route = HOME_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.AdminAccess.route
        ) {
            AdminAccess(navController = navController, webStart)
        }
        composable(
            route = Screen.Login.route
        ) {
            Login(navController = navController,context)
        }

        composable(
            route = Screen.GeneralWebView.route
        ) {
            GeneralWebView(navController = navController)
        }
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController = navController, wifi)
        }

        composable(
            route = Screen.KioskMode.route
        ) {
//            BackHandler(true) {
//
//
//            }

//            GestureOverlayView
//            (showPasswordDialog(context, ))
                KioskMode(navController = navController, lifecycle)

        }



        composable(
            route = Screen.Splash.route
        ) {
            SplashScreen(navController = navController)
        }
        composable(
            route = Screen.BlackList.route
        ) {
            BlackList(navController = navController)
        }

        composable(
            route = Screen.AllBrowsersList.route
        ) {
            AllBrowsersList(navController = navController)
        }

        composable(
            route = Screen.BlackListWeb.route
        ) {
            BlackListWeb(navController = navController)
        }

        composable(
            route = Screen.WhiteListWeb.route
        ) {
            WhiteListWeb(navController = navController)
        }

        composable(
            route = Screen.WhiteListWifi.route
        ) {
            WhiteListWifi(navController = navController)
        }

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
            SettingAdmin(navController = navController,activity)
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