package com.example.bibliored.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliored.controller.LibraryViewModel
import com.example.bibliored.controller.LibroViewModel
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Libro
import com.example.bibliored.view.BookDetailScreen
import com.example.bibliored.view.Portada
import kotlinx.coroutines.launch
import java.util.Collections

/*Esta Composable representa la pantalla principal del usuario logueado.
nombreCompleto: el nombre del usuario, para mostrar el saludo.
sessionPrefs: acceso al DataStore (para borrar sesión).
onLogout: callback que se ejecuta cuando se presiona “Salir”.
onAddClick: callback para navegar a la pantalla de Agregar / Escanear libro.
vm: tu LibraryViewModel, que expone los libros cargados (vm.libros).*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    nombreCompleto: String,
    sessionPrefs: SessionPrefs,
    onLogout: () -> Unit,
    onAddClick: () -> Unit
) {
    val ctx = LocalContext.current
    val sessionPrefs = remember(ctx) { SessionPrefs(ctx) }
    val vm = remember { LibraryViewModel(sessionPrefs = sessionPrefs) }

    val scope = rememberCoroutineScope() // scope: para lanzar corrutinas (usado al cerrar sesión).
    val libros = vm.libros.collectAsStateWithLifecycle().value /* libros: es la lista actual de libros, observada desde el ViewModel.
                                                                👉 Usa collectAsStateWithLifecycle para mantener el estado sincronizado.*/
    var selectedTab by remember { mutableStateOf(0) } /*selectedTab: controla qué pestaña está activa en el navbar inferior.
                                                               (por ahora, solo hay una pestaña: “Biblioteca”).*/
    var selectedLibro by remember { mutableStateOf<Libro?>(null) } // libro actualmente seleccionado para ver detalle

    /*Scaffold es el contenedor principal que organiza:
    topBar → barra superior (saludo).
    bottomBar → barra inferior (nav).
    floatingActionButton → botón flotante “+”.
    El contenido (entre { padding -> ... }).*/

    Scaffold(
        topBar = { //Muestra el saludo centrado con el nombre del usuario (viene desde Navigation.kt al logearse).
            CenterAlignedTopAppBar(
                title = { Text("Bienvenido, $nombreCompleto 👋") }
            )
        },
        floatingActionButton = { /* Muestra el botón flotante con el texto “+”.
                                    Cuando el usuario lo presiona, se llama onAddClick() → la app navega a la pantalla “Agregar libro”.*/
            FloatingActionButton(onClick = onAddClick) { Text("+") }
        },
        bottomBar = {
            NavigationBar { /*Primer botón → “Biblioteca”: activa la pestaña principal.
                            Segundo botón → “Salir”: limpia la sesión en DataStore y llama onLogout() (vuelve al login).
                            Se usa scope.launch { ... } porque sessionPrefs.clear() es una función suspendida (usa corrutina).*/
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Outlined.LibraryBooks, contentDescription = "Biblioteca") },
                    label = { Text("Biblioteca") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = {
                        scope.launch {
                            sessionPrefs.clear()
                            onLogout()
                        }
                    },
                    icon = { Icon(Icons.Outlined.Logout, contentDescription = "Cerrar sesión") },
                    label = { Text("Salir") }
                )
            }
        }
    ) { padding ->
        // Contenido principal
        if (selectedTab == 0) {
            when {
                selectedLibro != null -> {
                    val libros = vm.getLibros()
                    // Mostrar detalle del libro seleccionado y permitir volver
                    BookDetailScreen(libro = selectedLibro!!) { selectedLibro = null }
                }
                libros.isEmpty() -> {
                    EmptyLibrary( // Si la lista está vacía → muestra EmptyLibrary().
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        onAddClick = onAddClick
                    )
                }
                else -> {
                    LibraryGrid(
                        libros = libros,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        onLibroClick = { libro -> selectedLibro = libro }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyLibrary( //Muestra un mensaje centrado y un botón para ir a la pantalla de agregar libros.
    modifier: Modifier = Modifier,
    onAddClick: () -> Unit
) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Aún no tienes libros en tu biblioteca")
            Button(onClick = onAddClick) {
                Text("Agregar / Escanear libro")
            }
        }
    }
}

@Composable
private fun LibraryGrid(
    libros: List<Libro>,
    modifier: Modifier = Modifier,
    onLibroClick: (Libro) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        modifier = modifier
    ) {
        items(libros) { libro ->
            Card(Modifier.padding(8.dp).fillMaxWidth().clickable { onLibroClick(libro) }) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(8.dp)) {
                    Portada(libro = libro, modifier = Modifier.size(120.dp))
                    Spacer(Modifier.height(6.dp))
                    Text(libro.titulo, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun LibraryItem(libro: Libro) {
    /*Cada libro se muestra como una tarjeta (Card) con:
    - La portada (usando tu componente Portada),
    - El título del libro,
    - Y la lista de autores (si no tiene, muestra “Autor desconocido”).*/
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Portada(libro = libro, modifier = Modifier.size(72.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(libro.titulo, style = MaterialTheme.typography.titleMedium)
                val autores = libro.autores.joinToString { it.nombre }
                Text(
                    if (autores.isBlank()) "Autor desconocido" else autores,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
