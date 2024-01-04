package com.lock.applock.presentation.nav_graph

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.navigation.*
import androidx.navigation.compose.composable
import com.example.compse.ui.*
import com.lock.applock.presentation.activity.AdminAccess
import com.lock.applock.presentation.activity.AppManager
import com.lock.applock.presentation.activity.BlackList
import com.lock.applock.presentation.activity.BlackListWeb
import com.lock.applock.presentation.activity.GeneralWebView
import com.lock.applock.presentation.activity.LicenseActivation
import com.lock.applock.presentation.activity.Login
import com.lock.applock.presentation.activity.SettingScreen
import com.lock.applock.presentation.activity.WebManager
import com.lock.applock.presentation.screen.SplashScreen
import com.lock.applock.presentation.activity.WhiteList
import com.lock.applock.presentation.activity.WhiteListWeb
import com.lock.applock.presentation.activity.WhiteListWifi
import com.lock.applock.presentation.screen.NetworkControl
import com.lock.applock.presentation.screen.HomeScreen

@RequiresApi(34)
fun NavGraphBuilder.homeNavGraph(
    navController: NavHostController,
    activity : Activity, context:Context, wifi: () -> Unit

) {
    navigation(
        startDestination = Screen.AdminAccess.route,
        route = HOME_GRAPH_ROUTE
    ) {
        composable(
            route = Screen.AdminAccess.route
        ) {
            AdminAccess(navController = navController)
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
            route = Screen.LicenseActivation.route
        ) {
            LicenseActivation(navController = navController)
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
    }
}