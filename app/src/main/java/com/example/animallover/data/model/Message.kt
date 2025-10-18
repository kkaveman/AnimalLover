package com.example.animallover.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)