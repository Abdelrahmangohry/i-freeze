package com.ifreeze.data.repo.auth

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

interface AuthRepo {
    suspend fun getUserLogin( activationKey : String
                              ,deviceDto: DeviceDTO ):
            Response<String>

    suspend fun newUpdateUserData(deviceId: String): Response<Data>

    suspend fun userLocation(location: LocationModel): Response<Location>
    suspend fun mobileApps(apps: MobileApps): Response<MobileResponse>
    suspend fun unTrustedApps(): Response<Untrusted>
    suspend fun sendTicket(message: TicketMessageBody): Response<TicketResponse>
    suspend fun checkLicenseData(licenseID: String): Response<Boolean>

}