package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.model.MobileResponse
import com.lock.data.model.TicketMessageBody
import com.lock.data.model.TicketResponse
import com.lock.data.model.Untrusted
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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
