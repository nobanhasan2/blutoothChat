package com.nobanhasan.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nobanhasan.chat.adapter.viewHolder.ReceivedViewHolder
import com.nobanhasan.chat.adapter.viewHolder.SentViewHolder
import com.nobanhasan.chat.databinding.ReceivedLayoutBinding
import com.nobanhasan.chat.databinding.SentLayoutBinding
import com.nobanhasan.chat.model.Message
import com.nobanhasan.chat.util.Constants

/**
 * Created by
 * Noban Hasan on
 * 5/23/19.
 */

class ChatAdapter(private val chatList: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val SENT = 0
        const val RECEIVED = 1
    }

    override fun getItemViewType(position: Int) = when (chatList[position].type) {
        Constants.MESSAGE_TYPE_SENT -> SENT
        Constants.MESSAGE_TYPE_RECEIVED -> RECEIVED
        else -> -1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            SENT -> {
                val view = SentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return SentViewHolder(view)
            }

            RECEIVED -> {
                val view = ReceivedLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ReceivedViewHolder(view)
            }

            else -> {
                val view = SentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return SentViewHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            SENT -> {
                val sentViewHolder = holder as SentViewHolder
                sentViewHolder.bind(chatList[position])
            }

            RECEIVED -> {
                val receivedViewHolder = holder as ReceivedViewHolder
                receivedViewHolder.bind(chatList[position])
            }
        }
    }

    override fun getItemCount() = chatList.size
}