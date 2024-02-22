package com.lock.data.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class LocationModel(
    val location: String,
    val address: String,
    val deviceId: String
)

//send device apps

//z
data class MobileApps (
    @SerializedName("id"       ) var id       : String?           = null,
    @SerializedName("appName"  ) var appName  : List<String> = listOf(),
    @SerializedName("deviceId" ) var deviceId : String?           = null
)
//response
data class MobileResponse(
    val data: String,

)


//////////////////////////////////////
data class Location (
    @SerializedName("data"    ) var data    : LocationData?   = LocationData(),
)
data class LocationData (
    @SerializedName("location" ) var location : String? = null,
    @SerializedName("deviceId" ) var deviceId : String? = null
)
//////


data class LocationDataAddress(
    val address: String?,
    val latitude: Double?,
    val longitude: Double?
)