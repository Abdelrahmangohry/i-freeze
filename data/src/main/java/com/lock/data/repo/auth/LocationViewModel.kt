package com.lock.data.repo.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lock.data.model.LocationDataAddress
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationViewModel(private val repository: LocationRepository) : ViewModel() {
    private val _locationData = MutableLiveData<LocationDataAddress>()
    val locationData: LiveData<LocationDataAddress>
        get() = _locationData

    fun fetchLocation() {
        GlobalScope.launch {
            _locationData.value = repository.fetchLocation()
        }
    }
}