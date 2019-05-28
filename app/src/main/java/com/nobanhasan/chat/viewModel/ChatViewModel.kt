package com.nobanhasan.chat.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.nobanhasan.chat.model.Message

/**
 * Created by
 * Noban Hasan on
 * 5/26/19.
 */
class ChatViewModel : ViewModel() {

    var mList = MutableLiveData<List<Message>>()
    private val messageList = arrayListOf<Message>()
    fun updateChatList(message: Message) {
        messageList.add(message)
        mList.value = messageList
    }
}