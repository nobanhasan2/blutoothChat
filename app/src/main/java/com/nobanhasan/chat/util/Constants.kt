package com.nobanhasan.chat.util

/**
 * Created by
 * Noban Hasan on
 * 5/23/19.
 */

class Constants{

    companion object {

        // Message types sent from the BluetoothChatService Handler
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_DEVICE_NAME = 4
        val MESSAGE_TOAST = 5
        var MESSAGE_TYPE_SENT = 0
        var MESSAGE_TYPE_RECEIVED = 1

        // Key names received from the BluetoothChatService Handler
        val DEVICE_NAME = "device_name"
        val TOAST = "toast"

        const val ACTIVITY_SCOPE = "activity_scope"
        const val APPLICATION_SCOPE = "application_scope"
    }

}