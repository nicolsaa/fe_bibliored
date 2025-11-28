package com.example.bibliored.data.messages

import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message

class MessagesRepository {

    private val conversations = mutableListOf<Conversation>()

    private val messages = mutableMapOf<String, MutableList<Message>>()

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
