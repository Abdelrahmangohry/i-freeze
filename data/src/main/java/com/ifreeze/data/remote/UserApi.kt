package com.ifreeze.data.remote

import com.ifreeze.data.model.AlertBody
import com.ifreeze.data.model.AlertResponse
import com.ifreeze.data.model.BaseUlrResponse
import com.ifreeze.data.model.Data
import com.ifreeze.data.model.DeviceDTO
import com.ifreeze.data.model.Location
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.MobileConfigurationResponse
import com.ifreeze.data.model.MobileResponse
import com.ifreeze.data.model.ProactiveResultsBody
import com.ifreeze.data.model.ProactiveResultsResponse
import com.ifreeze.data.model.TicketMessageBody
import com.ifreeze.data.model.TicketResponse
import com.ifreeze.data.model.Untrusted
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface UserApi {
    @POST("Licenses/ActivateMobile/94156ef6-b6f3-4b3f-bed6-f4e077e99abd")
//    @POST("Licenses/ActivateMobile/c8ff0875-5dd4-4735-8694-56f69b01059a")
    suspend fun getUserLogin(
        @Body deviceDto: DeviceDTO
    ): Response<String>

//Get Mobile Configurations
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


    @GET("KioskApp")
    suspend fun getKioskApps(
    ): Response<MobileConfigurationResponse>

    @POST("alerts")
    suspend fun sendAlert(
        @Body message: List<AlertBody>
    ): Response<AlertResponse>

    @POST("ProactiveResults")
    suspend fun sendProactiveResults(
        @Body message:  List<ProactiveResultsBody>
    ): Response<ProactiveResultsResponse>

}

