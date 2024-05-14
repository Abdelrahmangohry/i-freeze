package com.ifreeze.applock.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifreeze.data.model.AppsModel
import com.ifreeze.domain.AppsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppsViewModel @Inject constructor(private val useCase: AppsUseCase) : ViewModel() {
    val _apps = MutableStateFlow<List<AppsModel>>(emptyList())
    val articlesItems: MutableStateFlow<List<AppsModel>> get() = _apps


    init {
        getAllApps()
    }

    fun insertApps(list: List<AppsModel>) {
        viewModelScope.launch {
            useCase.inertList(list)
        }
    }
    fun updateAppInList(updatedApp: AppsModel) {
        viewModelScope.launch {
            val updatedList = _apps.value.map { app ->
                if (app.packageName == updatedApp.packageName) updatedApp else app
            }
            _apps.value = updatedList
            useCase.insertApp(updatedApp)
        }
    }

    fun updateApp(app: AppsModel) {
        updateAppInList(app)
    }


    fun getAllApps() {
        viewModelScope.launch {
            _apps.value = useCase.getAllApps()
            Log.d("islam", "getAllApps : ${_apps.value} ")

            if (_apps.value.isEmpty()){
                insertApps(useCase.getAllAppsFromPhone())
                Log.d("islam", "getAllApps : inset ")
            }
        }
    }
}