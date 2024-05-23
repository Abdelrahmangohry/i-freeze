package com.ifreeze.data.remote

import com.ifreeze.data.model.BaseUlrResponse
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
    @POST("Licenses/ActivateMobile/c8ff0875-5dd4-4735-8694-56f69b01059a")
    suspend fun getUserLogin(
        @Body deviceDto: DeviceDTO
    ): Response<String>


    @GET("Devices/GetMobileConfigurations")
    suspend fun newUpdateUserData(
        @Query("mobileId")
        mobileId: String,
    ): Response<Data>

    @POST("MobileLocation")
    suspend fun userLocation(
        @Body location: LocationModel
    ): Response<Location>


    @POST("MobileApps")
    suspend fun mobileApps(
        @Body apps: MobileApps
    ): Response<MobileResponse>

    @GET("UntrustedApps")
    suspend fun unTrustedApps(
    ): Response<Untrusted>

    @POST("Ticket")
    suspend fun sendTicket(
        @Body message: TicketMessageBody
    ): Response<TicketResponse>

    @GET("Licenses/CheckLicenseDate/{licenseID}")
    suspend fun checkLicenseData(
        @Path("licenseID") licenseID: String,
    ): Response<Boolean>

    @GET("CloudURL/{licenseID}")
    suspend fun getCloudURL(
        @Path("licenseID") licenseID: String,
    ): Response<BaseUlrResponse>

}

