package com.lock.data.repo

import com.lock.data.model.AppsModel

interface ApksRepo {

    suspend fun saveApps(list : List<AppsModel>)

    suspend fun updateApp(app : AppsModel)
    suspend fun deleteApp(app : String)

    suspend fun getAllApps(): List<AppsModel>
    suspend fun getAllAppsFromDevice(): List<AppsModel>
}