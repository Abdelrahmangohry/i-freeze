package com.lock.domain

import com.lock.data.model.AppsModel
import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.model.MobileResponse
import com.lock.data.repo.auth.AuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val repo: AuthRepo) {
    suspend fun getUserLogin(
        activationKey: String, deviceDto: DeviceDTO
    ) : Response<String> {
        return repo.getUserLogin(activationKey, deviceDto)
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

}