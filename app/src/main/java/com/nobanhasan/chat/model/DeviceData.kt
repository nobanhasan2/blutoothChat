package com.nobanhasan.chat.model


/**
 * Created by Noban Hasan on 5/23/19.
 */

data class DeviceData(val deviceName: String?,val deviceHardwareAddress: String){

    override fun equals(other: Any?): Boolean {
        val deviceData = other as DeviceData
        return deviceHardwareAddress == deviceData.deviceHardwareAddress
    }

    override fun hashCode(): Int {
        return deviceHardwareAddress.hashCode()
    }

}