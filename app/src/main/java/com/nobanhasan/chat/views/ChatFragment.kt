package com.nobanhasan.chat.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.nobanhasan.chat.R
import com.nobanhasan.chat.adapter.ChatAdapter
import com.nobanhasan.chat.databinding.ChatFragmentBinding
import com.nobanhasan.chat.model.Message
import com.nobanhasan.chat.viewModel.ChatViewModel
import kotlinx.android.synthetic.main.chat_fragment.*

/**
 * Created by Noban Hasan on 5/26/19.
 */
class ChatFragment : Fragment(), View.OnClickListener {

    private lateinit var chatBinding: ChatFragmentBinding
    private lateinit var chatViewModel: ChatViewModel
    private var communicationListener: CommunicationListener? = null
    private var chatAdapter: ChatAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        chatViewModel = ViewModelProviders.of(
                this
        ).get(ChatViewModel::class.java)
        chatBinding = DataBindingUtil.inflate(
                inflater,
                R.layout.chat_fragment,
                container,
                false)
        initViews()
        return chatBinding.root
    }

    private fun initViews() {
        val llm = LinearLayoutManager(activity)
        llm.reverseLayout = true
        chatBinding.chatRecyclerView.layoutManager = llm

        // ... VM
        chatBinding.chatInput!!.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.isNotEmpty()) {
                    sendButton.isClickable = true
                    sendButton.isEnabled = true
                } else {
                    sendButton.isClickable = false
                    sendButton.isEnabled = false
                }
            }
        })

        // ... VM
        chatBinding.sendButton.setOnClickListener(this)

        chatViewModel.mList.observe(this, Observer<List<Message>> {
            it?.let {
                chatAdapter = ChatAdapter(it.reversed())
                chatRecyclerView.adapter = chatAdapter
                chatRecyclerView.scrollToPosition(0)
            }
        })
    }

    override fun onClick(p0: View?) {
        if (chatInput.text.isNotEmpty()) {
            communicationListener?.onCommunication(chatInput.text.toString())
            chatBinding.chatInput.setText("")
        }

    }

    fun setCommunicationListener(communicationListener: CommunicationListener) {
        this.communicationListener = communicationListener
    }

    interface CommunicationListener {
        fun onCommunication(message: String)
    }

    fun communicate(message: Message) {
        chatViewModel.updateChatList(message)
    }

    companion object {
        fun newInstance(): ChatFragment {
            val myFragment = ChatFragment()
            val args = Bundle()
            myFragment.arguments = args
            return myFragment
        }
    }

}