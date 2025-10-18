package com.example.animallover.data.model

class User {
    private var username : String = ""
    private var bio : String = ""
    private var uid : String = ""

    constructor()

    constructor(username: String, fullname: String, bio: String, uid: String) {
        this.username = username
        this.bio = bio
        this.uid = uid
    }

    fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getBio(): String {
        return bio
    }

    fun setBio(bio: String) {
        this.bio = bio
    }

    fun getUID(): String {
        return uid
    }

    fun setUID(uid: String) {
        this.uid = uid
    }
}
