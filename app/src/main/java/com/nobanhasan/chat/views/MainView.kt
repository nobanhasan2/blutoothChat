package com.nobanhasan.chat.views

import android.bluetooth.BluetoothDevice
import android.content.Intent
import com.nobanhasan.chat.model.DeviceData

interface MainView {

    fun makeVisible(discoverableIntent: Intent)

    fun pairAndConnect(deviceData: DeviceData)

    fun startDiscovery()

    fun searchFinished(mDeviceList: ArrayList<DeviceData>)

    fun chatCommunicate(writeMessage: String, milliSecondsTime: Long, messageE_TYPE_SENT: Int)

    fun showChatFragment()

    fun updateToolbar(conTo:String, status:Boolean)

    fun getAllPairedDevice(pairedDevices: Set<BluetoothDevice>)

    fun closeFragment()

}