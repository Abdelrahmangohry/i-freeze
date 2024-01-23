package com.lock.data.repo.auth

import com.lock.data.model.ApiResponse
import com.lock.data.model.AppsModel
import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthRepo {
    suspend fun getUserLogin( activationKey : String
                             ,deviceDto: DeviceDTO ):
            Response<String>

//    suspend fun getApplications() : Flow<Response<AppsModel>>

    suspend fun updateUserData(deviceID: String, deviceInfo: DeviceInfo): Flow<Response<Any>>
}