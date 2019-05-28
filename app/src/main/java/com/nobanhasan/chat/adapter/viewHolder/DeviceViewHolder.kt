package com.nobanhasan.chat.adapter.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.nobanhasan.chat.adapter.DevicesRecyclerViewAdapter
import com.nobanhasan.chat.databinding.DeviceBinding
import com.nobanhasan.chat.model.DeviceData


class DeviceViewHolder(private val bindingView: DeviceBinding) : RecyclerView.ViewHolder(bindingView.root) {

    fun bind(device: DeviceData, listener: DevicesRecyclerViewAdapter.ItemClickListener?) {
        bindingView.devicedata = device
        itemView?.setOnClickListener {
            listener?.itemClicked(device)
        }
    }
}
