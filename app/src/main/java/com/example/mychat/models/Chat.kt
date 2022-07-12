package com.example.mychat.models

data class Chat(
    var senderId: String = "",
    var receiverId: String = "",
    var message: String = ""
)
