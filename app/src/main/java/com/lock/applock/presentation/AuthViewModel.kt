package com.lock.applock.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.remote.NetWorkState
import com.lock.data.model.DeviceDTO
import com.lock.data.model.DeviceInfo
import com.lock.domain.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    val _loginFlow : MutableLiveData<Response<String>> = MutableLiveData()
//    val loginFlow = _loginFlow.asSharedFlow()
    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
        viewModelScope.launch {
            val response = useCase.getUserLogin(activationKey, deviceDto)
            _loginFlow.value = response
//            useCase.getUserLogin(activationKey, deviceDto)
//                .onStart {
//                _loginFlow.emit(NetWorkState.Loading)
//                Log.d("abdo", "Start")
//            }
//                .catch {
//                    _loginFlow.emit(NetWorkState.Error(it))
//                    Log.d("abdo", "Catch $it")
//
//                }
//                .onCompletion {
//                    _loginFlow.emit(NetWorkState.StopLoading)
//                    Log.d("abdo", "onCompletion $it")
//
//                }
//                .collectLatest {response ->
//                    Log.d("abdo", "getUserLogin: response ${response}")
//                    _loginFlow.emit(NetWorkState.Success(response))
//
//                }
        }
    }

//    fun updateUserData(
//        deviceID : String,
//        deviceInfo : DeviceInfo
//    ) {
//        viewModelScope.launch {
//            useCase.updateUserData(deviceID,deviceInfo)
//                .onStart {
//                    _loginFlow.emit(NetWorkState.Loading)
//                    Log.d("abdo", "Start UserData")
//                }
//                .catch {
//                    _loginFlow.emit(NetWorkState.Error(it))
//                    Log.d("abdo", "Catch $it UserData")
//
//                }
//                .onCompletion {
//                    _loginFlow.emit(NetWorkState.StopLoading)
//                    Log.d("abdo", "onCompletion $it UserData")
//
//                }
//                .collectLatest {
//                    Log.d("abdo", "getUserLogin: $it UserData")
//                    _loginFlow.emit(NetWorkState.Success(it))
//                }
//        }
//    }

}