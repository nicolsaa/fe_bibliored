package com.example.bibliored.controller.messages


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.data.messages.MessagesRepository
import com.example.bibliored.model.messages.Conversation
import com.example.bibliored.model.messages.Message
import com.example.bibliored.model.messages.BookInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MessagesRepository = MessagesRepository()
    private val sessionPrefs = SessionPrefs(application)

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _currentUserName = MutableStateFlow("Usuario")
    val currentUserName: StateFlow<String> = _currentUserName.asStateFlow()

    private val _currentUserId = MutableStateFlow("")

    init {
        viewModelScope.launch {
            sessionPrefs.sessionFlow.collect { session ->
                _currentUserName.value = session.userName
                _currentUserId.value = session.userId
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

            // Marcar conversación como leída al cargar mensajes
            val updatedConversations = _conversations.value.map { conversation ->
                if (conversation.id == conversationId && conversation.unreadCount > 0) {
                    conversation.copy(unreadCount = 0)
                } else {
                    conversation
                }
            }
            _conversations.value = updatedConversations
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        viewModelScope.launch {
            val newMessage = Message(
                id = System.currentTimeMillis().toString(),
                conversationId = conversationId,
                sender = _currentUserName.value,
                senderId = _currentUserId.value,
                content = content,
                timestamp = System.currentTimeMillis()
            )

            // Agregar mensaje al repositorio
            repository.addMessage(newMessage)
            _messages.value += newMessage

            // Actualizar última mensaje en la conversación
            updateConversationLastMessage(conversationId, content)
        }
    }

    // Función para crear conversación desde publicación de libro
    fun createConversationFromBook(
        otherUserId: String,
        otherUserName: String,
        bookTitle: String,
        coverUrl: String?
    ): String {
        val conversationId = "conv_${_currentUserId.value}_${otherUserId}_${System.currentTimeMillis()}"

        viewModelScope.launch {
            val newConversation = Conversation(
                id = conversationId,
                participants = listOf(_currentUserName.value, otherUserName),
                participantIds = listOf(_currentUserId.value, otherUserId),
                lastMessage = "Hola! Estoy interesado en tu libro \"$bookTitle\"",
                timestamp = System.currentTimeMillis(),
                unreadCount = 1, // El otro usuario tiene 1 mensaje no leído
                bookInfo = BookInfo(
                    title = bookTitle,
                    coverUrl = coverUrl
                )
            )

            // Crear mensaje inicial automático
            val initialMessage = Message(
                id = System.currentTimeMillis().toString(),
                conversationId = conversationId,
                sender = _currentUserName.value,
                senderId = _currentUserId.value,
                content = "Hola! Estoy interesado en tu libro \"$bookTitle\"",
                timestamp = System.currentTimeMillis()
            )

            // Guardar en el repositorio
            repository.addConversation(newConversation)
            repository.addMessage(initialMessage)

            // Actualizar el estado
            _conversations.value = listOf(newConversation) + _conversations.value
            _messages.value = listOf(initialMessage)
        }

        return conversationId
    }

    private fun updateConversationLastMessage(conversationId: String, lastMessage: String) {
        viewModelScope.launch {
            val updatedConversations = _conversations.value.map { conversation ->
                if (conversation.id == conversationId) {
                    conversation.copy(
                        lastMessage = lastMessage,
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    conversation
                }
            }.sortedByDescending { it.timestamp }

            _conversations.value = updatedConversations
        }
    }

    // Función para buscar conversaciones
    fun searchConversations(query: String) {
        viewModelScope.launch {
            val allConversations = repository.getConversations()
            if (query.isEmpty()) {
                _conversations.value = allConversations
            } else {
                val filtered = allConversations.filter { conversation ->
                    conversation.participants.any { it.contains(query, ignoreCase = true) } ||
                            conversation.lastMessage.contains(query, ignoreCase = true) ||
                            conversation.bookInfo?.title?.contains(query, ignoreCase = true) == true
                }
                _conversations.value = filtered
            }
        }
    }

    // Función para eliminar conversación
    fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            repository.deleteConversation(conversationId)
            _conversations.value = _conversations.value.filter { it.id != conversationId }

            // Si estábamos viendo los mensajes de esa conversación, limpiar
            if (_messages.value.isNotEmpty() && _messages.value.first().conversationId == conversationId) {
                _messages.value = emptyList()
            }
        }
    }

    // Función para obtener conversación por ID
    fun getConversationById(conversationId: String): Conversation? {
        return _conversations.value.find { it.id == conversationId }
    }
}