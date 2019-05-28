package com.nobanhasan.chat.adapter.viewHolder

import androidx.recyclerview.widget.RecyclerView
import com.nobanhasan.chat.databinding.SentLayoutBinding
import com.nobanhasan.chat.model.Message

class SentViewHolder(private val bindingView: SentLayoutBinding) : RecyclerView.ViewHolder(bindingView.root) {

    fun bind(message: Message) {
        bindingView.message = message
    }

}