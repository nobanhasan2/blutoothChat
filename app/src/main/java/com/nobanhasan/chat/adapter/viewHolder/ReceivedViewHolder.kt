package com.nobanhasan.chat.adapter.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.nobanhasan.chat.databinding.ReceivedLayoutBinding
import com.nobanhasan.chat.model.Message

class ReceivedViewHolder(private val bindingView: ReceivedLayoutBinding) : RecyclerView.ViewHolder(bindingView.root) {

    fun bind(message: Message) {
        bindingView.message = message
    }

}