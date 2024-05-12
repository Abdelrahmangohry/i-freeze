package com.example.compse.ui

import android.app.Activity
import android.content.Context
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.lock.applock.presentation.nav_graph.HOME_GRAPH_ROUTE
import com.lock.applock.presentation.nav_graph.ROOT_GRAPH_ROUTE
import com.lock.applock.presentation.nav_graph.homeNavGraph

@RequiresApi(34)
@Composable
fun SetupNavGraph(
    navController: NavHostController,
    activity: Activity,
    context: Context,
    wifi: () -> Unit,
    lifecycle: LifecycleOwner,
    webStart: () -> Unit,
    filesScan: () -> Unit

){
    NavHost(
        navController = navController,
        startDestination = HOME_GRAPH_ROUTE,
        route = ROOT_GRAPH_ROUTE
    ) {
        homeNavGraph(navController = navController  , activity,context,wifi, lifecycle,webStart,filesScan)
    }
}