package com.lock.data.model


//body send if the activation key is successfully
data class DeviceDTO(  val deviceName: String,
                       val operatingSystemVersion: String,
                       val deviceIp: String,
                       val macAddress: String,
                       val serialNumber: String)
