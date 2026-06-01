package com.example.bdu.model

data class ChatMessage(
    val text: String,
    val sender: Sender,
    val timestamp: Long = System.currentTimeMillis()
)

enum class Sender {
    USER,
    BOT
}
