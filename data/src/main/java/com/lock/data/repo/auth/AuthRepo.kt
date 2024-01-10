package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.DeviceDTO
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthRepo {
    suspend fun getUserLogin( activationKey : String
                             ,deviceDto: DeviceDTO ):
            Flow<Response<Any>>

//    suspend fun getApplications() : Flow<Response<AppsModel>>

}