package com.example.bibliored.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bibliored.model.Libro
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import com.example.bibliored.util.SelectedBookNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    libro: Libro? = null,
    onBack: (() -> Unit)? = null
) {
    val libroData = libro ?: SelectedBookNav.currentLibro
    if (libroData == null) {
        // No data to show
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(libroData.titulo) },
                navigationIcon = if (onBack != null) {
                    { IconButton(onClick = { onBack.invoke() }) { Icon(Icons.Filled.ArrowBack, contentDescription = "Back") } }
                } else {
                    { /* no back button */ }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Portada(libro = libroData, modifier = Modifier.fillMaxWidth().height(240.dp))
            Spacer(Modifier.height(12.dp))
            Text(libroData.titulo, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(4.dp))
            val autores = libroData.autores.joinToString { it.nombre }
            Text(autores, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(libroData.descripcion ?: "", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
