package com.ifreeze.applock.presentation.nav_graph

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.ifreeze.data.cash.PreferencesGateway


/**
 * Sets up the main navigation graph for the application using Jetpack Compose's NavHost.
 *
 * This composable function initializes the `NavHost` with the specified `NavController` and sets
 * the start destination to the route defined by `HOME_GRAPH_ROUTE`. It also configures the navigation
 * graph for the home section by calling the `homeNavGraph` function.
 *
 * @param navController The `NavHostController` used for managing navigation within the app.
 * @param activity The `Activity` context where navigation takes place.
 * @param context The context used for accessing application resources and other system services.
 * @param wifi A lambda function that handles WiFi-related actions.
 * @param lifecycle The `LifecycleOwner` used to manage the lifecycle of navigation components.
 * @param webStart A lambda function to initiate web-related actions.
 * @param fileScan A lambda function to handle file scanning actions.
 * @param preferences An instance of `PreferencesGateway` used for retrieving and saving user preferences.
 * @param screenShareFun A lambda function to handle screen sharing actions.
 */
@RequiresApi(34)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    activity: Activity,
    context: Context,
    wifi: () -> Unit,
    lifecycle: LifecycleOwner,
    webStart: () -> Unit,
    fileScan: () -> Unit,
    preferences: PreferencesGateway,
    screenShareFun: () -> Unit

){
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH_ROUTE, // Set the initial route for the navigation graph
        route = ROOT_GRAPH_ROUTE // Define the root route for the navigation graph
    ) {
        // Configure the home navigation graph with the provided parameters
        homeNavGraph(navController = navController  , activity,context,wifi, lifecycle,webStart, fileScan, preferences, screenShareFun)
    }
}