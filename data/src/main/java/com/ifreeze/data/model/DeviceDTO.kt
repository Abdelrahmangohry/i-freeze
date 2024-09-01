package com.ifreeze.data.model


//body of the device data send if the activation key is successfully
data class DeviceDTO(  val deviceName: String,
                       val operatingSystemVersion: String,
                       val deviceIp: String,
                       val macAddress: String,
                       val serialNumber: String)
