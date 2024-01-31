package com.lock.data.model

data class DeviceDTO(val deviceName: String,
                       val operatingSystemVersion: String,
                       val deviceIp: String,
                       val macAddress: String)

data class Data(val data: DeviceInfo)

data class DeviceInfo(val blockWiFi: Boolean,
                      val whiteListWiFi: Boolean,
                      val blockListURLs: Boolean,
                      val whiteListURLs: Boolean,
                      val blockListApps: Boolean,
                      val whiteListApps: Boolean,
                      val browsers: Boolean,
                     )
