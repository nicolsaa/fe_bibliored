package com.example.bibliored.data.messages

import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message

class MessagesRepository {

    private val conversations = mutableListOf<Conversation>(
        Conversation("1", listOf("Usuario1", "Usuario2"), participantIds = listOf("id1", "id2"), lastMessage = "Hola! ¿Cómo estás?", timestamp = System.currentTimeMillis() - 10000, unreadCount = 1),
        Conversation("2", listOf("Usuario1", "Usuario3"), participantIds = listOf("id1", "id3"), lastMessage = "Nos vemos mañana.", timestamp = System.currentTimeMillis() - 8000, unreadCount = 0)
    )

    private val messages = mutableMapOf<String, MutableList<Message>>(
        "1" to mutableListOf(
            Message("m1", "1", "Usuario2", "id2", "Hola! ¿Cómo estás?", System.currentTimeMillis() - 10000),
            Message("m2", "1", "Usuario1", "id1", "Todo bien, ¿y tú?", System.currentTimeMillis() - 5000)
        ),
        "2" to mutableListOf(
            Message("m3", "2", "Usuario3", "id3", "Nos vemos mañana.", System.currentTimeMillis() - 8000)
        )
    )

    fun getConversations(): List<Conversation> {
        return conversations
    }

    fun getMessages(conversationId: String): List<Message> {
        return messages[conversationId] ?: emptyList()
    }

    fun addMessage(message: Message) {
        val messageList = messages.getOrPut(message.conversationId) { mutableListOf() }
        messageList.add(message)
    }

    fun addConversation(conversation: Conversation) {
        conversations.add(0, conversation)
    }



    fun deleteConversation(conversationId: String) {
        conversations.removeAll { it.id == conversationId }
        messages.remove(conversationId)
    }
}