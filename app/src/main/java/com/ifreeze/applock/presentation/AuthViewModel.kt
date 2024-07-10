package com.ifreeze.applock.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifreeze.data.model.AlertBody
import com.ifreeze.data.model.AlertResponse
import com.ifreeze.data.model.BaseUlrResponse
import com.ifreeze.data.model.Data
import com.ifreeze.data.model.DeviceDTO
import com.ifreeze.data.model.Location
import com.ifreeze.data.model.LocationModel
import com.ifreeze.data.model.MobileApps
import com.ifreeze.data.model.MobileConfigurationResponse
import com.ifreeze.data.model.MobileResponse
import com.ifreeze.data.model.ProactiveResultsBody
import com.ifreeze.data.model.ProactiveResultsResponse
import com.ifreeze.data.model.TicketMessageBody
import com.ifreeze.data.model.TicketResponse
import com.ifreeze.data.model.Untrusted
import com.ifreeze.data.model.VersionsDetails
import com.ifreeze.domain.AuthUseCase
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
    var _sendTicketFlow: MutableLiveData<Response<TicketResponse>> = MutableLiveData()
    var _checkLicenseDataFlow: MutableLiveData<Response<Boolean>> = MutableLiveData()
    var _getcloudURL: MutableLiveData<Response<BaseUlrResponse>> = MutableLiveData()
    var _getkioskApps: MutableLiveData<Response<MobileConfigurationResponse>> = MutableLiveData()
    var _sendAlert: MutableLiveData<Response<AlertResponse>> = MutableLiveData()
    var _proactiveAlert: MutableLiveData<Response<ProactiveResultsResponse>> = MutableLiveData()
    var _getVersionsDetails: MutableLiveData<Response<VersionsDetails>> = MutableLiveData()


    fun getUserLogin(deviceDto: DeviceDTO) {
        viewModelScope.launch {
            val response = useCase.getUserLogin(deviceDto)
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

    fun sendTicket(message : TicketMessageBody) {
        viewModelScope.launch {
            val response7 = useCase.sendTicket(message)
            _sendTicketFlow.value = response7

        }
    }

    fun checkLicenseData(licenseID: String) {
        viewModelScope.launch {
            val response8 = useCase.checkLicenseData(licenseID)
            _checkLicenseDataFlow.value = response8

        }
    }

    fun getCloudURL(licenseID: String) {
        viewModelScope.launch {
            val response9 = useCase.getCloudURL(licenseID)
            _getcloudURL.value = response9

        }
    }

    fun getKioskApps() {
        viewModelScope.launch {
            val response10 = useCase.getKioskApps()
            _getkioskApps.value = response10

        }
    }

    fun sendAlert(message: List<AlertBody>
    ) {
        viewModelScope.launch {
            val response10 = useCase.sendAlert(message)
            _sendAlert.value = response10

        }
    }

    fun sendProactiveResults(message: List<ProactiveResultsBody>
    ) {
        viewModelScope.launch {
            val response10 = useCase.sendProactiveResults(message)
            _proactiveAlert.value = response10

        }
    }

    fun getAllVersionsDetails(num: Double, id: String) {
        viewModelScope.launch {
            val response11 = useCase.getAllVersionsDetails(num,id)
            _getVersionsDetails.value = response11

        }
    }

}
