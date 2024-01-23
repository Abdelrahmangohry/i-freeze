package com.lock.data.model

data class DeviceDTO(val deviceName: String,
                       val operatingSystemVersion: String,
                       val deviceIp: String,
                       val macAddress: String)


data class DeviceInfo(val BlockWiFi: Boolean,
                      val WhiteListWiFi: Boolean,
                      val BlockListURLs: Boolean,
                      val WhiteListURLs: Boolean,
                      val BlockListApps: Boolean,
                      val WhiteListApps: Boolean,
                      val Browsers: Boolean,
                     )

data class ApiResponse(
    val id: String?
)
