package com.nobanhasan.chat.util

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

object BindingUtil {

    @BindingAdapter("setChattingTime")
    @JvmStatic
    fun chatTime(textView: TextView, timeInMilliSeconds: Long?) {
        textView.text = "Sent at " + SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
        ).format(Date(timeInMilliSeconds!!))
    }
}