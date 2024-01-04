package com.lock.applock

import android.app.ActivityManager
import android.app.Application
import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AppLication : Application() {
    override fun onCreate() {
        super.onCreate()
    }

}