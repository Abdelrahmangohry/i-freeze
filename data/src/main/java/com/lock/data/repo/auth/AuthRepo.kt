package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.model.MobileResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface AuthRepo {
    suspend fun getUserLogin( activationKey : String
                              ,deviceDto: DeviceDTO ):
            Response<String>

    suspend fun newUpdateUserData(deviceId: String): Response<Data>

    suspend fun userLocation(location: LocationModel): Response<Location>
    suspend fun mobileApps(apps: MobileApps): Response<MobileResponse>

}