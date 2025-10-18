package com.example.animallover.model

data class ChatUser(
    val userId: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val lastMessage: String = "",
    val lastMessageTime: String = "",
    val unreadCount: Int = 0,
    val isOnline: Boolean = false
)