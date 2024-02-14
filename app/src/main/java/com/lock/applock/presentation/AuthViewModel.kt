package com.lock.applock.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.remote.NetWorkState
import com.lock.data.model.Data

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import com.lock.data.model.Location
import com.lock.data.model.LocationModel
import com.lock.domain.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
import retrofit2.Response

@HiltViewModel
class AuthViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {
    var _loginFlow: MutableLiveData<Response<String>> = MutableLiveData()
    var _newFlow: MutableLiveData<Response<Data>> = MutableLiveData()
    var _locationFlow: MutableLiveData<Response<Location>> = MutableLiveData()


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


}