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
import com.ifreeze.data.remote.UserApi
import retrofit2.Response
import javax.inject.Inject

class AuthRepoImp @Inject constructor(private val api: UserApi):AuthRepo {

    override suspend fun getUserLogin(
        activationKey: String,
        deviceDto: DeviceDTO
    ) : Response<String> {
        return api.
        getUserLogin(activationKey,deviceDto)
    }

    override suspend fun newUpdateUserData(deviceId: String): Response<Data> {
        return api.newUpdateUserData(deviceId)
    }


    override suspend fun userLocation(location: LocationModel): Response<Location> {
        return api.userLocation(location)
    }

    override suspend fun mobileApps(apps: MobileApps): Response<MobileResponse> {
        return api.mobileApps(apps)
    }

    override suspend fun unTrustedApps(): Response<Untrusted> {
        return api.unTrustedApps()
    }

    override suspend fun sendTicket(message: TicketMessageBody): Response<TicketResponse> {
        return api.sendTicket(message)
    }

    override suspend fun checkLicenseData(licenseID: String): Response<Boolean> {
        return api.checkLicenseData(licenseID)
    }
}
