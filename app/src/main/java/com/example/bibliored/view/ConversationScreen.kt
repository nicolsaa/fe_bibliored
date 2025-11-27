package com.example.bibliored.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliored.controller.messages.MessagesViewModel
import com.example.bibliored.model.messages.Message
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(
    conversationId: String,
    bookTitle: String?,
    coverUrl: String?,
    onBack: () -> Unit
) {
    val viewModel: MessagesViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    var newMessage by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
    }

    // Auto-scroll al enviar mensaje
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            ConversationTopBar(
                bookTitle = bookTitle,
                coverUrl = coverUrl,
                onBack = onBack
            )
        },
        bottomBar = {
            MessageInputBar(
                message = newMessage,
                onMessageChange = { newMessage = it },
                onSendMessage = {
                    if (newMessage.isNotBlank()) {
                        viewModel.sendMessage(conversationId, newMessage)
                        newMessage = ""
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (messages.isEmpty()) {
                EmptyConversationState()
            } else {
                MessagesList(
                    messages = messages,
                    currentUserName = currentUserName,
                    scrollState = scrollState
                )
            }

            // Header del libro flotante
            if (bookTitle != null) {
                FloatingBookInfo(
                    title = bookTitle,
                    coverUrl = coverUrl,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationTopBar(
    bookTitle: String?,
    coverUrl: String?,
    onBack: () -> Unit
) {
    TopAppBar(
        title = {
            if (bookTitle != null) {
                Column {
                    Text(
                        text = "Intercambio de libro",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = bookTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Text("Conversación")
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        actions = {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = "Portada del libro",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .padding(end = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    )
}

@Composable
fun MessagesList(
    messages: List<Message>,
    currentUserName: String,
    scrollState: LazyListState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState,
        reverseLayout = false,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(messages) { message ->
            MessageBubble(
                message = message,
                isFromMe = message.sender == currentUserName
            )
        }

        item {
            // Espacio adicional al final
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MessageBubble(message: Message, isFromMe: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
    ) {
        // Timestamp para mensajes consecutivos del mismo remitente
        if (!isFromMe) {
            Text(
                text = message.timestamp.toConversationTime(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f),
            horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.Bottom
        ) {
            if (!isFromMe) {
                // Avatar del remitente
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.sender.take(1).uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start
            ) {
                // Nombre del remitente (solo para mensajes de otros)
                if (!isFromMe) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }

                // Burbuja del mensaje
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (isFromMe) 16.dp else 4.dp,
                                bottomEnd = if (isFromMe) 4.dp else 16.dp
                            )
                        )
                        .background(
                            color = if (isFromMe) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isFromMe) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                // Timestamp del mensaje
                Text(
                    text = message.timestamp.toConversationTime(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun MessageInputBar(
    message: String,
    onMessageChange: (String) -> Unit,
    onSendMessage: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Campo de texto
            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                placeholder = {
                    Text(
                        "Escribe un mensaje...",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                ),
                shape = RoundedCornerShape(24.dp),
                singleLine = false,
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Botón de enviar
            IconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        color = if (message.isNotBlank()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                        }
                    ),
                enabled = message.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Enviar mensaje",
                    tint = if (message.isNotBlank()) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    },
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FloatingBookInfo(
    title: String,
    coverUrl: String?,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = "Portada del libro",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
            Text(
                text = "Intercambiando: $title",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun EmptyConversationState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Chat,
            contentDescription = "Conversación vacía",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Inicia la conversación",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Envía el primer mensaje para coordinar el intercambio",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// Funciones de extensión para formatear timestamps
private fun Long.toConversationTime(): String {
    // Implementa tu lógica de formateo de fecha/hora
    return "14:30" // Placeholder
}
