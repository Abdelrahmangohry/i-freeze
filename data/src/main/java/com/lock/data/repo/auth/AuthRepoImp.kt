package com.lock.data.repo.auth

import android.annotation.SuppressLint
import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.lock.data.model.AppsModel
import com.lock.data.model.Data

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.remote.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import retrofit2.http.Body
import java.util.Locale
import javax.inject.Inject

class AuthRepoImp @Inject constructor(private val api: UserApi, private val application: Application):AuthRepo {
    private val mFusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(application)
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



}




