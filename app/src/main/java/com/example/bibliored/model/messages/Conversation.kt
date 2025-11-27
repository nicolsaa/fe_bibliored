package com.example.bibliored.model.messages

data class Conversation(
    val id: String,
    val participants: List<String>,
    val lastMessage: String,
    val timestamp: Long, // Añadir timestamp
    val unreadCount: Int = 0, // Añadir contador de no leídos
    val participantIds: List<String> // Para los avatars
)