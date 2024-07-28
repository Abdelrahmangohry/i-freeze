package com.ifreeze.applock

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.work.Configuration
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.ifreeze.applock.service.AutoSyncWorker
import com.ifreeze.applock.utils.GlobalSettingsShare
import com.ifreeze.data.remote.UserApi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppLication : Application(), Configuration.Provider{
    @Inject
    lateinit var workerFactory: CustomWorkerFactory

    lateinit var globalSettings: GlobalSettingsShare

    override fun getWorkManagerConfiguration() =
                Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .build()
    /**
     * Gets global settings.
     *
     * @return the global settings
     */
    fun provideGlobalSettings(): GlobalSettingsShare {
        if (!::globalSettings.isInitialized) {
            globalSettings = GlobalSettingsShare()
        }
        return globalSettings
    }

}

class CustomWorkerFactory @Inject constructor(
    private val api: UserApi,

    ): WorkerFactory(){

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = AutoSyncWorker(api, appContext, workerParameters)

}