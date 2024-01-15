package com.lock.data.remote

import com.lock.data.model.DeviceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

//Api data base GET / POST
interface UserApi {
    @GET("Licenses/")
    suspend fun getUserLogin(
        @Path("id") activationKey: String,
        @Body deviceDto: DeviceDTO
    ): Response<Any>
}