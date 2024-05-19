package com.ifreeze.data.model

//get mobile configurations
data class Data(
    val data : Device,
)
data class Device(
    val device: DeviceConfigurations,
    val exceptionWifi: List<String>?,
    val blockedApps: List<String>,
    val exceptionApps: List<String>,
    val blockedWebsites: List<String>,
    val exceptionWebsites: List<String>
)
data class DeviceConfigurations(
    val blockWiFi : Boolean,
    val whiteListWiFi : Boolean,
    val blockListURLs : Boolean,
    val whiteListURLs : Boolean,
    val browsers : Boolean,
    val blockListApps : Boolean,
    val whiteListApps : Boolean,
    val licenseId : String,
    val time: String,


)
//////////////////////////////////


//post location to server
data class LocationModel(
    val location: String,
    val address: String,
    val deviceId: String
)


//location response
data class Location (
    var data    : LocationData,
)
data class LocationData (
    var location : String,
    var address : String,
    var deviceId : String
)
//////

//send device apps
data class MobileApps (
   var appName  : List<String>,
    var deviceId : String
)
//response
data class MobileResponse(
    val data: MobileApps,
    val status: Int,
    val message: String
)


data class LocationDataAddress(
    val address: String?,
    val latitude: Double?,
    val longitude: Double?
)

//Untrusted Applications
data class Untrusted(
    val data: List<UntrustedApps>,
    val status: Int,
    val message: String
)
data class UntrustedApps(
    val id: String,
    val appName : String
)


//Send Ticket
data class TicketMessageBody(
    val deviceId: String,
    val name: String,
    val email: String,
    val phone: String,
    val description: String
)

data class TicketResponse (
    var data    : String,
)