package com.lock.data.repo.auth

import com.lock.data.model.AppsModel
import com.lock.data.model.DeviceDTO
import com.lock.data.remote.UserApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

class AuthRepoImp @Inject constructor(private val api: UserApi):AuthRepo {
    override suspend fun getUserLogin(
        activationKey: String,
        deviceDto: DeviceDTO
    ) = flow {
            emit(api.getUserLogin(activationKey,deviceDto))
    }

//    override suspend fun getApplications() = flow {
//        emit(api.)
//    }
}