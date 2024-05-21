package com.ifreeze.domain

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
import com.ifreeze.data.repo.auth.AuthRepo
import retrofit2.Response
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val repo: AuthRepo) {
    suspend fun getUserLogin(
        deviceDto: DeviceDTO
    ) : Response<String> {
        return repo.getUserLogin(deviceDto)
    }

    suspend fun newUpdateUserData(
        mobileId : String
    ) : Response<Data> {
        return repo.newUpdateUserData(mobileId)
    }

    suspend fun userLocation(
        location: LocationModel
    ) : Response<Location> {
        return repo.userLocation(location)
    }

    suspend fun  mobileApps(
        apps: MobileApps
    ) : Response<MobileResponse> {
        return repo.mobileApps(apps)
    }

    suspend fun unTrustedApps(
    ) : Response<Untrusted> {
        return repo.unTrustedApps()
    }

    suspend fun  sendTicket(
        message: TicketMessageBody
    ) : Response<TicketResponse> {
        return repo.sendTicket(message)
    }

    suspend fun  checkLicenseData(
        licenseID: String
    ) : Response<Boolean> {
        return repo.checkLicenseData(licenseID)
    }

    suspend fun  getCloudURL(
        licenseID: String
    ) : Response<BaseUlrResponse> {
        return repo.getCloudURL(licenseID)
    }
}
