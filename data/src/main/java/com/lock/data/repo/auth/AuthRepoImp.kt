package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.model.MobileResponse
import com.lock.data.model.Untrusted
import com.lock.data.remote.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.GET
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
}
