package com.example.bibliored.model.messages

// Conversation.kt - Añade los nuevos campos
data class Conversation(
    val id: String,
    val participants: List<String>,
    val participantIds: List<String>, // Nuevo campo
    val lastMessage: String,
    val timestamp: Long,
    val unreadCount: Int = 0, // Nuevo campo
    val bookInfo: BookInfo? = null // Nuevo campo
)
