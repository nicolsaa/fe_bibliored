package com.example.bibliored.api

import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message

class MessagesRepository {

    // Almacenamiento temporal en memoria (en una app real, esto sería una base de datos)
    private val conversations = mutableListOf<Conversation>()
    private val messages = mutableListOf<Message>()

    init {
        // Datos de ejemplo
        conversations.addAll(
            listOf(
                Conversation(
                    id = "conv_1",
                    participants = listOf("Ana García", "Carlos López"),
                    participantIds = listOf("user_ana", "user_carlos"),
                    lastMessage = "¡Hola! ¿Te interesa intercambiar libros?",
                    timestamp = System.currentTimeMillis() - 3600000,
                    unreadCount = 2
                ),
                Conversation(
                    id = "conv_2",
                    participants = listOf("María Rodríguez", "Pedro Sánchez"),
                    participantIds = listOf("user_maria", "user_pedro"),
                    lastMessage = "Perfecto, nos vemos mañana en la biblioteca",
                    timestamp = System.currentTimeMillis() - 86400000,
                    unreadCount = 0
                )
            )
        )

        messages.addAll(
            listOf(
                Message(
                    id = "1",
                    conversationId = "conv_1",
                    sender = "Carlos López",
                    senderId = "user_carlos",
                    content = "Hola Ana, vi que tienes el libro 'Cien Años de Soledad'",
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                Message(
                    id = "2",
                    conversationId = "conv_1",
                    sender = "Ana García",
                    senderId = "user_ana",
                    content = "¡Hola Carlos! Sí, lo tengo disponible para intercambiar",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
        )
    }

    suspend fun getConversations(): List<Conversation> {
        return conversations.sortedByDescending { it.timestamp }
    }

    suspend fun getMessages(conversationId: String): List<Message> {
        return messages
            .filter { it.conversationId == conversationId }
            .sortedBy { it.timestamp }
    }

    suspend fun addMessage(message: Message) {
        messages.add(message)
    }

    suspend fun addConversation(conversation: Conversation) {
        conversations.add(0, conversation) // Agregar al inicio
    }

    suspend fun deleteConversation(conversationId: String) {
        conversations.removeAll { it.id == conversationId }
        messages.removeAll { it.conversationId == conversationId }
    }

    suspend fun updateConversation(conversation: Conversation) {
        val index = conversations.indexOfFirst { it.id == conversation.id }
        if (index != -1) {
            conversations[index] = conversation
        }
    }
}