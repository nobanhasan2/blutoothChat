package com.nobanhasan.chat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nobanhasan.chat.adapter.viewHolder.DeviceViewHolder
import com.nobanhasan.chat.databinding.DeviceBinding
import com.nobanhasan.chat.model.DeviceData

/**
 * Created by Noban Hasan on 5/23/19.
 */

class DevicesRecyclerViewAdapter(val mDeviceList: List<DeviceData>, val context: Context) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var listener: ItemClickListener? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val deviceViewHolder = holder as DeviceViewHolder
        deviceViewHolder.bind(mDeviceList[position],listener)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = DeviceBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return DeviceViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mDeviceList.size
    }


    fun setItemClickListener(listener: ItemClickListener){
        this.listener = listener
    }

    interface ItemClickListener{
        fun itemClicked(deviceData: DeviceData)
    }
}