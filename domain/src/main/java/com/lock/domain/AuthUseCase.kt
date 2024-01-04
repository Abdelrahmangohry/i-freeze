package com.lock.domain

import com.lock.data.model.DeviceDTO
import com.lock.data.repo.auth.AuthRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthUseCase @Inject constructor(private val repo: AuthRepo) {
    suspend fun getUserLogin(
        activationKey: String, deviceDto: DeviceDTO
    ) : Flow<Any?>{
      return repo.getUserLogin(activationKey,
          deviceDto).map { it.body() }
    }

}