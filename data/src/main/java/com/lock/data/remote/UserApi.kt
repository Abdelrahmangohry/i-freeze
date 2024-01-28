package com.lock.data.remote

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// UserApi interface
interface UserApi {
    @POST("api/Licenses/ActivateDevice/{activationKey}")
    suspend fun getUserLogin(
        @Path("activationKey") activationKey: String,
        @Body deviceDto: DeviceDTO
    ): Response<String>

    @GET("api/Devices/{id}")
    suspend fun updateUserData(
        @Path("id")
        id : String,
    ) : Response<DeviceInfo>

}

