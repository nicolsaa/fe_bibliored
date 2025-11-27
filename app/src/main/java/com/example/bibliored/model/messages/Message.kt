package com.example.bibliored.model.messages

data class Message(
    val id: String,
    val conversationId: String,
    val sender: String,
    val content: String,
    val timestamp: Long
)