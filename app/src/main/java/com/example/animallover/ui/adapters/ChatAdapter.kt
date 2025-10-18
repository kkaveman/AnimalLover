package com.example.animallover.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.animallover.model.ChatUser
import com.example.animallover.R

class ChatAdapter(
    private var chatList: List<ChatUser>,
    private val onItemClick: (ChatUser) -> Unit
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.ivProfileImage)
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val lastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        val lastMessageTime: TextView = itemView.findViewById(R.id.tvLastMessageTime)
        val unreadCount: TextView = itemView.findViewById(R.id.tvUnreadCount)
        val onlineIndicator: View = itemView.findViewById(R.id.onlineIndicator)

        fun bind(chatUser: ChatUser) {
            userName.text = chatUser.name
            lastMessage.text = chatUser.lastMessage
            lastMessageTime.text = chatUser.lastMessageTime

            // Load profile image
            if (chatUser.profileImageUrl.isNotEmpty()) {
                Glide.with(itemView.context)
                    .load(chatUser.profileImageUrl)
                    .placeholder(R.drawable.cat_icon)
                    .error(R.drawable.cat_icon)
                    .into(profileImage)
            } else {
                profileImage.setImageResource(R.drawable.cat_icon)
            }

            // Show/hide unread count
            if (chatUser.unreadCount > 0) {
                unreadCount.visibility = View.VISIBLE
                unreadCount.text = if (chatUser.unreadCount > 99) "99+" else chatUser.unreadCount.toString()
            } else {
                unreadCount.visibility = View.GONE
            }

            // Show/hide online indicator
            onlineIndicator.visibility = if (chatUser.isOnline) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                onItemClick(chatUser)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_user, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int = chatList.size

    fun updateList(newList: List<ChatUser>) {
        chatList = newList
        notifyDataSetChanged()
    }
}