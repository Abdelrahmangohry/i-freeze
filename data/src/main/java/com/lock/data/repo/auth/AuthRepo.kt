package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.Data

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthRepo {
    suspend fun getUserLogin( activationKey : String
                             ,deviceDto: DeviceDTO ):
            Response<String>

    suspend fun newUpdateUserData(deviceId: String): Response<Data>
}


