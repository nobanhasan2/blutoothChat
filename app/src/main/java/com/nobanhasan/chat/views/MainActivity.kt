package com.nobanhasan.chat.views

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.nobanhasan.chat.App
import com.nobanhasan.chat.R
import com.nobanhasan.chat.adapter.DevicesRecyclerViewAdapter
import com.nobanhasan.chat.chatService.BluetoothChatService
import com.nobanhasan.chat.databinding.ActivityMainBinding
import com.nobanhasan.chat.di.mainActivity.MainModule
import com.nobanhasan.chat.model.DeviceData
import com.nobanhasan.chat.viewModel.MainViewModel
import com.nobanhasan.chat.viewModel.factories.MainViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * Created by Noban Hasan on 5/26/19.
 */

class MainActivity : AppCompatActivity(), DevicesRecyclerViewAdapter.ItemClickListener,
        ChatFragment.CommunicationListener, MainView {


    lateinit var app:App
    @Inject
    lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private val REQUEST_ENABLE_BT = 123
    private var mBtAdapter: BluetoothAdapter? = null
    private var mChatService: BluetoothChatService? = null
    private val PERMISSION_REQUEST_LOCATION = 123
    private val PERMISSION_REQUEST_LOCATION_KEY = "PERMISSION_REQUEST_LOCATION"
    private var alreadyAskedForPermission = false
    private lateinit var chatFragment: ChatFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = App.getApplication(this)
        initComponents()

        if (savedInstanceState != null) {
            alreadyAskedForPermission = savedInstanceState.getBoolean(PERMISSION_REQUEST_LOCATION_KEY, false)
        }
        if (!mainViewModel.mBtAdapter.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        } else {
            status.text = getString(R.string.not_connected)
        }
        mainViewModel.makeVisibleDevice()
        checkPermissions()

        mainViewModel.liveDeviceList.observe(this, Observer<List<DeviceData>> {
            it?.let {
                val devicesAdapter = DevicesRecyclerViewAdapter(context = this, mDeviceList = it as ArrayList<DeviceData>)
                recyclerView.adapter = devicesAdapter
                devicesAdapter.setItemClickListener(this)
            }

        })

    }

    private fun registerBroadcastReceiver() {
        val intent = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mainViewModel.mPairReceiver, intent)

        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mainViewModel.mReceiver, filter)

        filter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        this.registerReceiver(mainViewModel.mReceiver, filter)
    }

    private fun initComponents() {


        mainViewModel = ViewModelProviders.of(
                this,
                MainViewModelFactory(this)
        ).get(MainViewModel::class.java)
        binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main)
        // Get the local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter()
        mChatService = BluetoothChatService(this, mainViewModel.mHandler)
        app.appComponent.plus(MainModule(this)).inject(this)
        registerBroadcastReceiver()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false
    }

    override fun makeVisible(discoverableIntent: Intent) {
        startActivity(discoverableIntent)
    }

    override fun searchFinished(mDeviceList: ArrayList<DeviceData>) {
        progressBar.visibility = View.INVISIBLE
        toolbarTitle.text = getString(R.string.all_device)
        headerLabelContainer.visibility = View.GONE
        if (mDeviceList.size == 0) {
            mainViewModel.getPairedDevices()
        }
    }

    override fun updateToolbar(conTo: String, status: Boolean) {
        if (status) {
            toolbarTitle.text = conTo
            Snackbar.make(findViewById(R.id.mainScreen), getString(R.string.connected_to_) + conTo, Snackbar.LENGTH_SHORT).show()
        } else {
            this.status.text = getString(R.string.not_connected)
            Snackbar.make(findViewById(R.id.mainScreen), getString(R.string.not_connected), Snackbar.LENGTH_SHORT).show()
        }

    }

    override fun startDiscovery() {
        progressBar.visibility = View.VISIBLE
        toolbarTitle.text = getString(R.string.searching)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(PERMISSION_REQUEST_LOCATION_KEY, alreadyAskedForPermission)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        when (requestCode) {

            PERMISSION_REQUEST_LOCATION -> {
                // the request returned a result so the dialog is closed
                alreadyAskedForPermission = false
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Log.d(TAG, "Coarse and fine location permissions granted")
                    mainViewModel.startDiscovery()
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle(getString(R.string.fun_limted))
                        builder.setMessage(getString(R.string.since_perm_not_granted))
                        builder.setPositiveButton(android.R.string.ok, null)
                        builder.show()
                    }
                }
            }
        }
    }

    override fun itemClicked(deviceData: DeviceData) {
        connectDevice(deviceData)
        mainViewModel.pairAndConnect(deviceData)
        mainViewModel.getPairedDevices()
    }

    override fun pairAndConnect(deviceData: DeviceData) {
        connectDevice(deviceData)
        mainViewModel.getPairedDevices()
    }

    override fun chatCommunicate(writeMessage: String, milliSecondsTime: Long, messageE_TYPE_SENT: Int) {
        chatFragment.communicate(com.nobanhasan.chat.model.Message(writeMessage, milliSecondsTime, messageE_TYPE_SENT))
    }

    override fun onResume() {
        super.onResume()
        val intent = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        registerReceiver(mainViewModel.mPairReceiver, intent)
        if (mChatService != null) {
            if (mChatService?.getState() == BluetoothChatService.STATE_NONE) {
                mChatService?.start()
            }
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mainViewModel.mReceiver)
    }

    override fun showChatFragment() {

        if (!isFinishing) {
            recyclerView.visibility=View.GONE
            val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            chatFragment = ChatFragment.newInstance()
            chatFragment.setCommunicationListener(this)
            fragmentTransaction.replace(R.id.mainScreen, chatFragment, getString(R.string.chatFragment))
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }
    }

    override fun closeFragment() {
        recyclerView.visibility=View.VISIBLE
        supportFragmentManager.beginTransaction().remove(chatFragment).commit()
    }

    override fun onCommunication(message: String) {
        sendMessage(message)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0)
            super.onBackPressed()
        else
            supportFragmentManager.beginTransaction().remove(chatFragment).commit()
            recyclerView.visibility=View.VISIBLE
            toolbarTitle.text ="Paired Devices"
    }

    override fun getAllPairedDevice(pairedDevices: Set<BluetoothDevice>) {
        if (pairedDevices.isNotEmpty()) {
            toolbarTitle.text = getString(R.string.paired_devices)
        }
    }

    private fun checkPermissions() {

        if (alreadyAskedForPermission) {
            // don't check again because the dialog is still open
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check 
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {

                val builder = AlertDialog.Builder(this)
                builder.setTitle(getString(R.string.need_loc_access))
                builder.setMessage(getString(R.string.please_grant_loc_access))
                builder.setPositiveButton(android.R.string.ok, null)
                builder.setOnDismissListener {
                    // the dialog will be opened so we have to save that
                    alreadyAskedForPermission = true
                    requestPermissions(arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    ), PERMISSION_REQUEST_LOCATION)
                }
                builder.show()

            } else {
                mainViewModel.startDiscovery()
            }
        } else {
            mainViewModel.startDiscovery()
            alreadyAskedForPermission = true
        }

    }

    private fun connectDevice(deviceData: DeviceData) {
        mBtAdapter?.cancelDiscovery()
        val deviceAddress = deviceData.deviceHardwareAddress
        val device = mBtAdapter?.getRemoteDevice(deviceAddress)
        mChatService?.connect(device, true)

    }

    private fun sendMessage(message: String) {
        if (this.mChatService?.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show()
            return
        }
        if (message.isNotEmpty()) {
            val send = message.toByteArray()
            mChatService?.write(send)

        }
    }
}
