package com.ifreeze.data.repo.auth

import com.ifreeze.data.model.BaseUlrResponse
import com.ifreeze.data.model.Data
import com.ifreeze.data.model.DeviceDTO
import com.ifreeze.data.model.Location
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.MobileConfigurationResponse
import com.ifreeze.data.model.MobileResponse
import com.ifreeze.data.model.TicketMessageBody
import com.ifreeze.data.model.TicketResponse
import com.ifreeze.data.model.Untrusted
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface AuthRepo {
    suspend fun getUserLogin(deviceDto: DeviceDTO ): Response<String>

    suspend fun newUpdateUserData(deviceId: String): Response<Data>

    suspend fun userLocation(location: LocationModel): Response<Location>
    suspend fun mobileApps(apps: MobileApps): Response<MobileResponse>
    suspend fun unTrustedApps(): Response<Untrusted>
    suspend fun sendTicket(message: TicketMessageBody): Response<TicketResponse>
    suspend fun checkLicenseData(licenseID: String): Response<Boolean>
    suspend fun getCloudURL(licenseID: String): Response<BaseUlrResponse>

    suspend fun getKioskApps(): Response<MobileConfigurationResponse>
}
