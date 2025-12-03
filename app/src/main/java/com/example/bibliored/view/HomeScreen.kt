package com.example.bibliored.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Autor
import com.example.bibliored.model.Libro
import com.example.bibliored.model.PortadaUrl
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.background
import com.example.bibliored.controller.BookViewModel
import com.example.bibliored.view.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nombreCompleto: String,
    sessionPrefs: SessionPrefs,
    bookViewModel: BookViewModel,
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onBookContactClick: (Libro, String) -> Unit, // Cambiado para incluir userId
    onProfileClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    val ctx = LocalContext.current
    val homeBooks by bookViewModel.homeBooks.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Bienvenido, $nombreCompleto 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.MAIN,
                onHomeClick = { /* Ya estás en Home */ },
                onBibliotecaClick = onBibliotecaClick,
                onMessagesClick = onMessagesClick,
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar libro",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { padding ->
        BookFeed(
            libros = homeBooks,
            onContactClick = onBookContactClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
private fun BookFeed(
    libros: List<Libro>,
    onContactClick: (Libro, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(libros) { libro ->
            BookPostItem(
                libro = libro,
                onContactClick = onContactClick,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun BookPostItem(
    libro: Libro,
    onContactClick: (Libro, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Header estilo Instagram con usuario
            UserHeader(
                userName = libro.nombreUsuario ?: "Usuario",
                userAvatar = null, // Puedes añadir avatar si lo tienes
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Portada del libro
            Portada(
                libro = libro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            )

            // Acciones (like, contactar)
            PostActions(
                isLiked = isLiked,
                onLikeClick = { isLiked = !isLiked },
                onContactClick = {
                    libro.userId?.let { userId ->
                        onContactClick(libro, userId)
                    }
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Información del libro
            BookInfo(
                libro = libro,
                isExpanded = isExpanded,
                onExpandClick = { isExpanded = !isExpanded },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun UserHeader(
    userName: String,
    userAvatar: String?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar del usuario
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(18.dp))
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
                text = userName.take(1).uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PostActions(
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onContactClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onLikeClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = "Like",
                    tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Button(
            onClick = onContactClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Message,
                contentDescription = "Contactar",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contactar")
        }
    }
}

@Composable
private fun BookInfo(
    libro: Libro,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // Título y autor
        Text(
            text = libro.titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = libro.autores.joinToString { it.nombre },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Descripción expandible
        Column(
            modifier = Modifier
                .animateContentSize()
                .clickable { onExpandClick() }
        ) {
            Text(
                text = libro.descripcion ?: "No hay descripción disponible.",
                style = MaterialTheme.typography.bodyLarge,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (!isExpanded && (libro.descripcion?.length ?: 0) > 150) {
                Text(
                    text = "Ver más",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
