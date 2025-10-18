package com.example.animallover.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.animallover.R
import com.example.animallover.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    private var messageList: List<Message>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.tvMessageText)
        val messageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: Message) {
            messageText.text = message.message
            messageTime.text = formatTime(message.timestamp)
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageText: TextView = itemView.findViewById(R.id.tvMessageText)
        val messageTime: TextView = itemView.findViewById(R.id.tvMessageTime)

        fun bind(message: Message) {
            messageText.text = message.message
            messageTime.text = formatTime(message.timestamp)
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]
        return if (message.senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_sent, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_message_received, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messageList[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messageList.size

    private fun formatTime(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp

        val now = Calendar.getInstance()

        return if (isSameDay(calendar, now)) {
            SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        } else {
            SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(calendar.time)
        }
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }
}