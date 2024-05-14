package com.ifreeze.data.remote

import com.ifreeze.data.model.Data
import com.ifreeze.data.model.DeviceDTO
import com.ifreeze.data.model.Location
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.MobileResponse
import com.ifreeze.data.model.TicketMessageBody
import com.ifreeze.data.model.TicketResponse
import com.ifreeze.data.model.Untrusted
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

//Api data base GET / POST
interface UserApi {
    @POST("api/Licenses/ActivateMobile/{activationKey}")
    suspend fun getUserLogin(
        @Path("activationKey") activationKey: String,
        @Body deviceDto: DeviceDTO
    ): Response<String>


    @GET("api/Devices/GetMobileConfigurations")
    suspend fun newUpdateUserData(
        @Query("mobileId")
        mobileId: String,
    ): Response<Data>

    @POST("api/MobileLocation")
    suspend fun userLocation(
        @Body location: LocationModel
    ): Response<Location>


    @POST("api/MobileApps")
    suspend fun mobileApps(
        @Body apps: MobileApps
    ): Response<MobileResponse>

    @GET("api/UntrustedApps")
    suspend fun unTrustedApps(
    ): Response<Untrusted>

    @POST("api/Ticket")
    suspend fun sendTicket(
        @Body message: TicketMessageBody
    ): Response<TicketResponse>

    @GET("api/Licenses/CheckLicenseDate/{licenseID}")
    suspend fun checkLicenseData(
        @Path("licenseID") licenseID: String,
    ): Response<Boolean>

}

