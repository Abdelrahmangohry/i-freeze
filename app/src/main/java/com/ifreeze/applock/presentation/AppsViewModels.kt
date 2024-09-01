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

/**
 * ViewModel for managing and interacting with a list of apps.
 *
 * This ViewModel is responsible for retrieving, updating, and inserting apps into the repository.
 * It also exposes the current list of apps as a [StateFlow] to the UI.
 *
 * @property useCase The [AppsUseCase] used for performing operations related to apps.
 */
@HiltViewModel
class AppsViewModel @Inject constructor(private val useCase: AppsUseCase) : ViewModel() {
    // Backing property for the list of apps
    val _apps = MutableStateFlow<List<AppsModel>>(emptyList())

    // Exposed state flow to observe the list of apps
    val articlesItems: MutableStateFlow<List<AppsModel>> get() = _apps


    /**
     * Initializes the ViewModel by retrieving all apps.
     */
    init {
        getAllApps()
    }

    /**
     * Inserts a list of apps into the repository.
     *
     * @param list The list of [AppsModel] to be inserted.
     */
    fun insertApps(list: List<AppsModel>) {
        viewModelScope.launch {
            useCase.inertList(list)
        }
    }

    /**
     * Updates a specific app in the list and the repository.
     *
     * @param updatedApp The [AppsModel] instance containing updated information.
     */
    fun updateAppInList(updatedApp: AppsModel) {
        viewModelScope.launch {
            // Create a new list with the updated app
            val updatedList = _apps.value.map { app ->
                if (app.packageName == updatedApp.packageName) updatedApp else app
            }
            // Update the state flow with the new list
            _apps.value = updatedList
            // Update the repository
            useCase.insertApp(updatedApp)
        }
    }

    /**
     * Updates an app in the list by delegating to [updateAppInList].
     *
     * @param app The [AppsModel] instance to be updated.
     */
    fun updateApp(app: AppsModel) {
        updateAppInList(app)
    }


    /**
     * Retrieves all apps from the repository and updates the state flow.
     * If the list is empty, inserts apps retrieved from the phone.
     */
    fun getAllApps() {
        viewModelScope.launch {
            _apps.value = useCase.getAllApps()

            if (_apps.value.isEmpty()){
                insertApps(useCase.getAllAppsFromPhone())
            }
        }
    }
}