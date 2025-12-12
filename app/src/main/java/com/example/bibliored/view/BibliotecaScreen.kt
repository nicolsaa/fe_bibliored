package com.example.bibliored.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bibliored.controller.LibraryViewModel
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.data.dataStore
import com.example.bibliored.model.Libro
import com.example.bibliored.view.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BibliotecaScreen(
    onAddClick: () -> Unit,
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLibroClick: (Libro) -> Unit
) {
    val ctx = LocalContext.current
    val sessionPrefs = remember(ctx) { SessionPrefs(ctx.dataStore) }
    val vm = remember { LibraryViewModel(sessionPrefs = sessionPrefs) }

    val libros by vm.libros.collectAsStateWithLifecycle()
    LaunchedEffect(libros) { if (libros.isEmpty()) vm.getLibros() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Biblioteca") }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.BIBLIOTECA,
                onHomeClick = onHomeClick,
                onBibliotecaClick = onBibliotecaClick,
                onMessagesClick = onMessagesClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        if (libros.isEmpty()) {
            EmptyLibrary(
                modifier = Modifier.fillMaxSize().padding(padding),
                onAddClick = onAddClick
            )
        } else {
            LibraryGrid(
                libros = libros,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                onLibroClick = onLibroClick
            )
        }
    }
}

@Composable
private fun EmptyLibrary(
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
