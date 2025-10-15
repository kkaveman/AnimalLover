package com.example.animallover.data

data class CommunityPost(
    val id: String,
    val userName: String,
    val userProfileImageUrl: String,
    val postText: String,
    val postImageUrl: String? = null,
    val likes: Int,
    val comments: Int
)
