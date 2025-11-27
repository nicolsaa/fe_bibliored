package com.example.bibliored.data.messages

import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message

class MessagesRepository {

    fun getConversations(): List<Conversation> {
        return listOf(
            Conversation("1", listOf("Usuario1", "Usuario2"), "Hola! ¿Cómo estás?", System.currentTimeMillis() - 10000, 1, listOf("Usuario1", "Usuario2")),
            Conversation("2", listOf("Usuario1", "Usuario3"), "Nos vemos mañana.", System.currentTimeMillis() - 8000, 0, listOf("Usuario1", "Usuario3"))
        )
    }

    fun getMessages(conversationId: String): List<Message> {
        return when (conversationId) {
            "1" -> listOf(
                Message("m1", "1", "Usuario2", "Hola! ¿Cómo estás?", System.currentTimeMillis() - 10000),
                Message("m2", "1", "Usuario1", "Todo bien, ¿y tú?", System.currentTimeMillis() - 5000)
            )
            "2" -> listOf(
                Message("m3", "2", "Usuario3", "Nos vemos mañana.", System.currentTimeMillis() - 8000)
            )
            else -> emptyList()
        }
    }
}
