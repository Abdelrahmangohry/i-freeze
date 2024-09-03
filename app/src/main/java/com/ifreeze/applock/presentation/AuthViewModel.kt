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

/**
 * ViewModel for handling authentication-related operations and data.
 *
 * This ViewModel manages the interactions with the [AuthUseCase] for various authentication and data operations.
 * It exposes multiple [MutableLiveData] properties to observe the results of these operations.
 *
 * @property useCase The [AuthUseCase] used for performing authentication and data operations.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(private val useCase: AuthUseCase) : ViewModel() {
    // LiveData for user login response
    var _loginFlow: MutableLiveData<Response<String>> = MutableLiveData()

    // LiveData for new user data update response
    var _newFlow: MutableLiveData<Response<Data>> = MutableLiveData()

    // LiveData for user location response
    var _locationFlow: MutableLiveData<Response<Location>> = MutableLiveData()

    // LiveData for mobile apps response
    var _mobileAppsFlow: MutableLiveData<Response<MobileResponse>> = MutableLiveData()

    // LiveData for untrusted apps response
    var _untrustedAppsFlow: MutableLiveData<Response<Untrusted>> = MutableLiveData()

    // LiveData for sending ticket response
    var _sendTicketFlow: MutableLiveData<Response<TicketResponse>> = MutableLiveData()

    // LiveData for license data check response
    var _checkLicenseDataFlow: MutableLiveData<Response<Boolean>> = MutableLiveData()

    // LiveData for cloud URL response
    var _getcloudURL: MutableLiveData<Response<BaseUlrResponse>> = MutableLiveData()

    // LiveData for kiosk apps response
    var _getkioskApps: MutableLiveData<Response<MobileConfigurationResponse>> = MutableLiveData()

    // LiveData for sending alert response
    var _sendAlert: MutableLiveData<Response<AlertResponse>> = MutableLiveData()

    // LiveData for proactive alert response
    var _proactiveAlert: MutableLiveData<Response<ProactiveResultsResponse>> = MutableLiveData()

    // LiveData for versions details response
    var _getVersionsDetails: MutableLiveData<Response<VersionsDetails>> = MutableLiveData()


    /**
     * Retrieves user login information using the provided [DeviceDTO].
     *
     * @param deviceDto The [DeviceDTO] containing the device details for login.
     */
    fun getUserLogin(activationKey: String, deviceDto: DeviceDTO) {
        viewModelScope.launch {
            val response = useCase.getUserLogin(activationKey, deviceDto)
            _loginFlow.value = response

        }
    }

    /**
     * Updates user data with the provided device ID.
     *
     * @param deviceId The ID of the device to update user data.
     */
    fun newUpdateUserData(deviceId: String) {
        viewModelScope.launch {
            val response3 = useCase.newUpdateUserData(deviceId)
            _newFlow.value = response3
        }
    }

    /**
     * Sends user location data.
     *
     * @param location The [LocationModel] containing the user location details.
     */
    fun userLocation(location: LocationModel) {
        viewModelScope.launch {
            val response4 = useCase.userLocation(location)
            _locationFlow.value = response4

        }
    }

    /**
     * Sends mobile apps data.
     *
     * @param apps The [MobileApps] containing details about mobile applications.
     */
    fun mobileApps(apps: MobileApps) {
        viewModelScope.launch {
            val response5 = useCase.mobileApps(apps)
            _mobileAppsFlow.value = response5

        }
    }

    /**
     * Retrieves untrusted apps data.
     */
    fun unTrustedApps() {
        viewModelScope.launch {
            val response6 = useCase.unTrustedApps()
            _untrustedAppsFlow.value = response6

        }
    }

    /**
     * Sends a ticket with the provided message body.
     *
     * @param message The [TicketMessageBody] containing the ticket details.
     */
    fun sendTicket(message : TicketMessageBody) {
        viewModelScope.launch {
            val response7 = useCase.sendTicket(message)
            _sendTicketFlow.value = response7
        }
    }

    /**
     * Checks the license data with the provided license ID.
     *
     * @param licenseID The ID of the license to check.
     */
    fun checkLicenseData(licenseID: String) {
        viewModelScope.launch {
            val response8 = useCase.checkLicenseData(licenseID)
            _checkLicenseDataFlow.value = response8
        }
    }

    /**
     * Retrieves the cloud URL with the provided license ID.
     *
     * @param licenseID The ID of the license to get the cloud URL.
     */
    fun getCloudURL(licenseID: String) {
        viewModelScope.launch {
            val response9 = useCase.getCloudURL(licenseID)
            _getcloudURL.value = response9

        }
    }

    /**
     * Retrieves kiosk apps configuration.
     */
    fun getKioskApps() {
        viewModelScope.launch {
            val response10 = useCase.getKioskApps()
            _getkioskApps.value = response10

        }
    }

    /**
     * Sends an alert with the provided list of alert bodies.
     *
     * @param message The list of [AlertBody] containing alert details.
     */
    fun sendAlert(message: List<AlertBody>
    ) {
        viewModelScope.launch {
            val response10 = useCase.sendAlert(message)
            _sendAlert.value = response10

        }
    }

    /**
     * Sends proactive results with the provided list of proactive results bodies.
     *
     * @param message The list of [ProactiveResultsBody] containing proactive results details.
     */
    fun sendProactiveResults(message: List<ProactiveResultsBody>
    ) {
        viewModelScope.launch {
            val response10 = useCase.sendProactiveResults(message)
            _proactiveAlert.value = response10

        }
    }

    /**
     * Retrieves all versions details with the provided number and ID.
     *
     * @param num The version number.
     * @param id The ID of the item to get version details.
     */
    fun getAllVersionsDetails(num: Double, id: String) {
        viewModelScope.launch {
            val response11 = useCase.getAllVersionsDetails(num,id)
            _getVersionsDetails.value = response11

        }
    }
}
