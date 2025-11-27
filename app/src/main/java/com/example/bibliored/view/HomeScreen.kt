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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nombreCompleto: String,
    sessionPrefs: SessionPrefs,
    onLogout: () -> Unit,
    onAddClick: () -> Unit,
    onBookContactClick: (Libro) -> Unit,
    onProfileClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit
) {
    val ctx = LocalContext.current
    val sessionPrefs = remember(ctx) { SessionPrefs(ctx) }

    val mockLibros = listOf(
        Libro(
            isbn10 = "1234567890",
            isbn13 = "1234567890123",
            titulo = "El Señor de los Anillos",
            autores = listOf(Autor(nombre = "J.R.R. Tolkien")),
            descripcion = "Una gran aventura en la Tierra Media que narra el viaje del hobbit Frodo Bolsón para destruir el Anillo Único y derrotar al Señor Oscuro, Sauron. Acompañado por una comunidad diversa de hobbits, elfos, enanos y hombres, Frodo debe atravesar peligrosas tierras y enfrentarse a sus miedos más profundos.",
            portada = PortadaUrl(
                small = "https://covers.openlibrary.org/b/id/10308969-S.jpg",
                medium = "https://covers.openlibrary.org/b/id/10308969-M.jpg",
                large = "https://covers.openlibrary.org/b/id/10308969-L.jpg"
            ),
            workKey = "/works/OL45804W",
            editionKey = "/books/OL26336839M",
            nombreUsuario = "Juan Perez"
        ),
        Libro(
            isbn10 = "0987654321",
            isbn13 = "0987654321098",
            titulo = "Cien Años de Soledad",
            autores = listOf(Autor(nombre = "Gabriel García Márquez")),
            descripcion = "La novela narra la historia de la familia Buendía a lo largo de siete generaciones en el pueblo ficticio de Macondo. Considerada una obra maestra de la literatura hispanoamericana y universal, es una de las obras más traducidas y leídas en español.",
            portada = PortadaUrl(
                small = "https://covers.openlibrary.org/b/id/8264768-S.jpg",
                medium = "https://covers.openlibrary.org/b/id/8264768-M.jpg",
                large = "https://covers.openlibrary.org/b/id/8264768-L.jpg"
            ),
            workKey = "/works/OL45883W",
            editionKey = "/books/OL24351648M",
            nombreUsuario = "Maria Rodriguez"
        ),
        Libro(
            isbn10 = null,
            isbn13 = null,
            titulo = "Libro sin Portada",
            autores = listOf(Autor(nombre = "Autor Anónimo")),
            descripcion = "Un libro misterioso sin portada y con una descripción muy breve.",
            portada = null,
            workKey = null,
            editionKey = null,
            nombreUsuario = "Pedro Pascal"
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bienvenido, $nombreCompleto 👋") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                onHomeClick = { /* Ya estás en Home */ },
                onBibliotecaClick = onBibliotecaClick,
                onMessagesClick = onMessagesClick,
                onProfileClick = onProfileClick
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        }
    ) { padding ->
        BookFeed(
            libros = mockLibros,
            onContactClick = onBookContactClick,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        )
    }
}

@Composable
fun BottomNavigationBar(
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = true, // Siempre seleccionado en la pantalla de inicio
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Book, contentDescription = "Biblioteca") },
            label = { Text("Biblioteca") },
            selected = false,
            onClick = onBibliotecaClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Messages") },
            label = { Text("Mensajes") },
            selected = false,
            onClick = onMessagesClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Perfil") },
            selected = false,
            onClick = onProfileClick
        )
    }
}


@Composable
private fun BookFeed(
    libros: List<Libro>,
    onContactClick: (Libro) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(libros) { libro ->
            BookPostItem(
                libro = libro,
                onContactClick = onContactClick,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun BookPostItem(
    libro: Libro,
    onContactClick: (Libro) -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            Portada(
                libro = libro,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )
            Column(Modifier.padding(16.dp)) {
                Text(libro.titulo, style = MaterialTheme.typography.titleLarge)
                Text(libro.autores.joinToString { it.nombre }, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                libro.nombreUsuario?.let {
                    Text(
                        text = "Publicado por: $it",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .animateContentSize()
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Text(
                        text = libro.descripcion ?: "No hay descripción disponible.",
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }


                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { isLiked = !isLiked }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                    Button(onClick = { onContactClick(libro) }) {
                        Text("Contactar")
                    }
                }
            }
        }
    }
}
