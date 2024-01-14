package com.lock.applock.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.remote.NetWorkState
import com.lock.data.model.DeviceDTO
import com.lock.domain.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {
    private val _loginFlow = MutableStateFlow<NetWorkState>(NetWorkState.Loading)
    val loginFlow = _loginFlow.asSharedFlow()
    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
        viewModelScope.launch {
            useCase.getUserLogin(activationKey, deviceDto).onStart {
                _loginFlow.emit(NetWorkState.Loading)
                Log.d("abdo", "Start")
            }
                .catch {
                    _loginFlow.emit(NetWorkState.Error(it))
                    Log.d("abdo", "Catch $it")

                }
                .onCompletion {
                    _loginFlow.emit(NetWorkState.StopLoading)
                    Log.d("abdo", "onCompletion $it")

                }
                .collectLatest {
                    Log.d("abdo", "getUserLogin: ${it}")
                    _loginFlow.emit(NetWorkState.Success(it))
                }
        }
    }



}