// src/main/java/com/example/miniproject/model/Message.kt
package com.example.miniproject.model

data class Message(
    val userId: String? = null,
    val displayName: String? = null,
    val messageText: String? = null,
    val timestamp: Long? = null
)
