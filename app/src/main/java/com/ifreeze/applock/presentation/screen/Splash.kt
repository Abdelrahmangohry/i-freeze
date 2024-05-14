package com.ifreeze.applock.presentation.screen

import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ifreeze.applock.R
import com.ifreeze.applock.helper.getListApps
import com.ifreeze.applock.presentation.AppsViewModel
import com.ifreeze.applock.presentation.nav_graph.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

@Composable
fun SplashScreen(navController: NavController , viewModel: AppsViewModel=hiltViewModel()) {

   val context=LocalContext.current

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 2000,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        val appsList = withContext(Dispatchers.IO) {
           context.getListApps()
        }

        // Now you have the apps list, you can insert it into the database
        viewModel.insertApps(appsList)
        Log.d("islam", "appsList : ${appsList} ")

        Log.d("islam", "SplashScreen : ${viewModel.getAllApps()} ")
        delay(1000L)
        navController.navigate(Screen.Home.route){
           navController.popBackStack()
        }
    }


    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.playstore),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}
