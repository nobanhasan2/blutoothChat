package com.nobanhasan.chat.model

/**
 * Created by
 * Noban Hasan on
 * 5/23/19.
 */

data class Message(
        var message: String,
        var time: Long,
        var type: Int
)