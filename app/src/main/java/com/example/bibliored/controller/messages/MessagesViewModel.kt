package com.example.bibliored.controller.messages

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.data.messages.MessagesRepository
import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MessagesRepository = MessagesRepository()
    private val sessionPrefs = SessionPrefs(application)

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _currentUserName = MutableStateFlow("Usuario")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    init {
        viewModelScope.launch {
            sessionPrefs.sessionFlow.collect {
                _currentUserName.value = it.userName
            }
        }
    }

    fun loadConversations() {
        viewModelScope.launch {
            _conversations.value = repository.getConversations()
        }
    }

    fun loadMessages(conversationId: String) {
        viewModelScope.launch {
            _messages.value = repository.getMessages(conversationId)
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        viewModelScope.launch {
            val newMessage = Message(
                id = (messages.value.size + 1).toString(),
                conversationId = conversationId,
                sender = _currentUserName.value,
                content = content,
                timestamp = System.currentTimeMillis()
            )
            _messages.value = _messages.value + newMessage
        }
    }
}
