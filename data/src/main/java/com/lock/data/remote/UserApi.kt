package com.lock.data.remote


import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// UserApi interface
interface UserApi {

    @POST("api/Licenses/ActivateMobile/{activationKey}")
    suspend fun getUserLogin(
        @Path("activationKey") activationKey: String,
        @Body deviceDto: DeviceDTO
    ): Response<String>


    @GET("api/Devices/GetMobileConfigurations")
    suspend fun newUpdateUserData(
        @Query("mobileId")
        mobileId : String,
    ) : Response<Data>

}

