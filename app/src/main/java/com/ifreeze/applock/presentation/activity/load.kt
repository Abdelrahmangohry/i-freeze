package com.ifreeze.applock.presentation.activity

import BG_trans_light
import PrimaryColor
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

@Composable
fun LoadingLayer() {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = PrimaryColor
    ) {
        Center {
            CircularProgressIndicator(
                modifier = Modifier.semantics {
                    this.contentDescription="ContentsDescriptions"
                },
                color = BG_trans_light
            )
        }
    }
}


@Composable
fun Center(
    content: @Composable () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}