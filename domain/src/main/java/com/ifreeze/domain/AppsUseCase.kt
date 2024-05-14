package com.ifreeze.domain

import com.ifreeze.data.model.AppsModel
import com.ifreeze.data.repo.ApksRepo
import javax.inject.Inject
//Use Case
class AppsUseCase @Inject constructor(private val repo: ApksRepo){
    suspend fun inertList(list : List<AppsModel>){
        repo.saveApps(list)
    }

    suspend fun insertApp(app : AppsModel){
        repo.updateApp(app)
    }

    suspend fun getAllApps()=repo.getAllApps()
    suspend fun getAllAppsFromPhone()=repo.getAllAppsFromDevice()

    suspend fun deleteApp(app :String){
        repo.deleteApp(app)
    }
}