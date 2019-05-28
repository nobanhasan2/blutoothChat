package com.nobanhasan.chat.viewModel

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobanhasan.chat.chatService.BluetoothChatService
import com.nobanhasan.chat.model.DeviceData
import com.nobanhasan.chat.util.Constants
import com.nobanhasan.chat.views.MainView
import javax.inject.Inject

/**
 * Created by
 * Noban Hasan on
 * 5/26/19.
 */
class MainViewModel @Inject constructor(
        val mainView: MainView
) : ViewModel() {

    var liveDeviceList = MutableLiveData<List<DeviceData>>()
    private val mDeviceList = arrayListOf<DeviceData>()
    val mBtAdapter = BluetoothAdapter.getDefaultAdapter()!!
    private var mConnectedDeviceName: String = ""
    var connected= ObservableField<Boolean>()

    @Throws(Exception::class)
    fun createBond(btDevice: BluetoothDevice): Boolean {
        val class1 = Class.forName("android.bluetooth.BluetoothDevice")
        val createBondMethod = class1.getMethod("createBond")
        return createBondMethod.invoke(btDevice) as Boolean

    }
    fun makeVisibleDevice() {
        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        mainView.makeVisible(discoverableIntent)
    }

    fun pairAndConnect(deviceData: DeviceData) {
        var isBonded = false
        val blDevice: BluetoothDevice = mBtAdapter!!.getRemoteDevice(deviceData.deviceHardwareAddress)
        try {
            isBonded = createBond(blDevice)
            if (isBonded) {
                mainView.pairAndConnect(deviceData)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun startDiscovery() {
        mainView.startDiscovery()
        mDeviceList.clear()
        if (mBtAdapter.isDiscovering)
            mBtAdapter.cancelDiscovery()
        mBtAdapter.startDiscovery()

    }
    fun getPairedDevices() {
        val pairedDevices = mBtAdapter.bondedDevices
        mDeviceList.clear()
        if (pairedDevices?.size ?: 0 > 0) {
            for (device in pairedDevices!!) {
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                mDeviceList.add(DeviceData(deviceName, deviceHardwareAddress))
            }
            liveDeviceList.value = mDeviceList
            mainView.getAllPairedDevice(pairedDevices)
        }
    }



    val mReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val action = intent.action

            if (BluetoothDevice.ACTION_FOUND == action) {
                val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                val deviceName = device.name
                val deviceHardwareAddress = device.address // MAC address
                val deviceData = DeviceData(deviceName, deviceHardwareAddress)
                mDeviceList.add(deviceData)
                val setList = HashSet<DeviceData>(mDeviceList)
                mDeviceList.clear()
                mDeviceList.addAll(setList)

            }
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                mainView.searchFinished(mDeviceList)
                liveDeviceList.value = mDeviceList
                Log.e("Size Searched", mDeviceList.size.toString())
            }
        }
    }
    val mPairReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED == action) {
                val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
                val prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)
                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    getPairedDevices()
                }
            }
        }
    }
    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    val mHandler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                Constants.MESSAGE_STATE_CHANGE -> {
                    when (msg.arg1) {

                        BluetoothChatService.STATE_CONNECTED -> {
                            mainView.updateToolbar(mConnectedDeviceName, true)
                            connected.set(true)
                        }
                        BluetoothChatService.STATE_CONNECTING -> {
                            connected.set(false)
                        }
                        BluetoothChatService.STATE_LISTEN, BluetoothChatService.STATE_NONE -> {
                            mainView.updateToolbar(mConnectedDeviceName, false)
                            connected.set(false)
                        }
                    }
                }
                Constants.MESSAGE_WRITE -> {
                    val writeBuf = msg.obj as ByteArray
                    val writeMessage = String(writeBuf)
                    val milliSecondsTime = System.currentTimeMillis()
                    mainView.chatCommunicate(writeMessage, milliSecondsTime, Constants.MESSAGE_TYPE_SENT)
                }
                Constants.MESSAGE_READ -> {
                    val readBuf = msg.obj as ByteArray
                    val readMessage = String(readBuf, 0, msg.arg1)
                    val milliSecondsTime = System.currentTimeMillis()
                    mainView.chatCommunicate(readMessage, milliSecondsTime, Constants.MESSAGE_TYPE_RECEIVED)

                }
                Constants.MESSAGE_DEVICE_NAME -> {
                    // save the connected device's name
                    mConnectedDeviceName = msg.data.getString(Constants.DEVICE_NAME)
                    mainView.updateToolbar(mConnectedDeviceName, true)
                    connected.set(true)
                    mainView.showChatFragment()
                }
                Constants.MESSAGE_TOAST -> {
                    mainView.updateToolbar(mConnectedDeviceName, false)
                    connected.set(false)
                }
            }
        }
    }

}