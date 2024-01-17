package com.lock.data.remote

import com.lock.data.model.DeviceDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//Api data base GET / POST
interface UserApi {
    @POST("api/Licenses/ActivateDevice/{activationKey}94156ef6-b6f3-4b3f-bed6-f4e077e99abd")
    suspend fun getUserLogin(
       @Path("activationKey")
        activationKey: String="94156ef6-b6f3-4b3f-bed6-f4e077e99abd",

        @Body deviceDto: DeviceDTO
    ): Response<Any>
}