package com.example.bibliored.model.messages

data class Conversation(
    val id: String,
    val participants: List<String>,
    val lastMessage: String
)