package com.lock.data.model

import android.net.Uri

data class LocationModel(
    val location: String,
    val address: String,
    val deviceId: String
)

//send device apps
data class MobileApps(
    val appName: List<String>,
    val deviceId: String

)
//response
data class MobileResponse(
    val data: String,

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
    val latitude: Double?,
    val longitude: Double?
)