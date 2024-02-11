package com.lock.domain

import android.util.Log
import com.lock.data.model.AppsModel
import com.lock.data.model.Data

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import com.lock.data.repo.auth.AuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

}
