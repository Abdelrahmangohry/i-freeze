package com.lock.data.remote

import com.lock.data.model.ApiResponse
import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

// UserApi interface
interface UserApi {
    @POST("api/Licenses/ActivateDevice/{activationKey}")
    suspend fun getUserLogin(
        @Path("activationKey") activationKey: String,
        @Body deviceDto: DeviceDTO
    ): Response<String>

    @PUT("api/Devices/{id}")
    suspend fun updateUserData(
        @Path("id")
        deviceID : String,
        @Body deviceInfo : DeviceInfo

    ) : Response<Any>




}

