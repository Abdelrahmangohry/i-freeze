package com.lock.applock.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lock.data.model.Data
import com.lock.data.model.DeviceDTO
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.data.model.MobileApps
import com.lock.data.model.MobileResponse
import com.lock.data.model.Untrusted
import com.lock.domain.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {
    var _loginFlow: MutableLiveData<Response<String>> = MutableLiveData()
    var _newFlow: MutableLiveData<Response<Data>> = MutableLiveData()
    var _locationFlow: MutableLiveData<Response<Location>> = MutableLiveData()

    var _mobileAppsFlow: MutableLiveData<Response<MobileResponse>> = MutableLiveData()
    var _untrustedAppsFlow: MutableLiveData<Response<Untrusted>> = MutableLiveData()


    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
        viewModelScope.launch {
            val response = useCase.getUserLogin(activationKey, deviceDto)
            _loginFlow.value = response

        }
    }


    fun newUpdateUserData(deviceId: String) {
        viewModelScope.launch {
            val response3 = useCase.newUpdateUserData(deviceId)
            _newFlow.value = response3
        }
    }

    fun userLocation(location: LocationModel) {
        viewModelScope.launch {
            val response4 = useCase.userLocation(location)
            _locationFlow.value = response4

        }
    }

    fun mobileApps(apps: MobileApps) {
        viewModelScope.launch {
            val response5 = useCase.mobileApps(apps)
            _mobileAppsFlow.value = response5

        }
    }

    fun unTrustedApps() {
        viewModelScope.launch {
            val response6 = useCase.unTrustedApps()
            _untrustedAppsFlow.value = response6

        }
    }


}



