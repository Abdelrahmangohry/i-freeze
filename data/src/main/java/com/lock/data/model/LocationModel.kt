package com.lock.data.model

data class LocationModel(
    val location: String,
    val deviceId: String
)


//ViewModelResponse
data class Location(
    val data: LocationData,
)

data class LocationData(
    val location: String?,
    val deviceId: String?,
)
//////


data class LocationDataAddress(
    val address: String?,
    val errorMessage: String?
)