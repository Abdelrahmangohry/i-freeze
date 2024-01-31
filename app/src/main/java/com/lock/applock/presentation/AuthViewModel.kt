package com.lock.applock.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.remote.NetWorkState
import com.lock.data.model.Data

import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
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
    var _updateFlow: MutableLiveData<Response<Data>> = MutableLiveData()
    var _newFlow: MutableLiveData<Response<Data>> = MutableLiveData()

    //    val loginFlow = _loginFlow.asSharedFlow()
    /////
//    private val _loginFlow = MutableStateFlow<Response<String>?>(null)
//    val loginFlow: StateFlow<Response<String>?> get() = _loginFlow
//
//    private val _updateFlow = MutableStateFlow<Response<DeviceInfo>?>(null)
//    val updateFlow: StateFlow<Response<DeviceInfo>?> get() = _updateFlow

    //
    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
        viewModelScope.launch {
            val response = useCase.getUserLogin(activationKey, deviceDto)
            _loginFlow.value = response

        }
    }



//    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
//        viewModelScope.launch {
//            _loginFlow.value = useCase.getUserLogin(activationKey, deviceDto)
//        }
//    }
//
//    fun updateUserData(deviceID: String) {
//        viewModelScope.launch {
//            _updateFlow.value = useCase.updateUserData(deviceID)
//        }
//    }

    fun updateUserData(deviceID: String) {
        viewModelScope.launch {
            val response2 = useCase.updateUserData(deviceID)
            _updateFlow.value = response2

        }
    }

    fun newUpdateUserData(deviceId: String) {
        viewModelScope.launch {
            val response = useCase.updateUserData(deviceId)
            _newFlow.value = response

        }
    }

}
