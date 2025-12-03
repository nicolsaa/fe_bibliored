package com.example.bibliored.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.bibliored.model.Libro
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import com.example.bibliored.util.SelectedBookNav

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    libro: Libro? = null,
    onBack: (() -> Unit)? = null,
    onPublish: ((Libro) -> Unit)? = null,
    onDone: (() -> Unit)? = null
) {
    val libroData = libro ?: SelectedBookNav.currentLibro
    if (libroData == null) {
        // No data to show
        return
    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(libroData.titulo) },
                navigationIcon = {
                    IconButton(onClick = { onBack?.invoke() ?: backDispatcher?.onBackPressed() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {
            Portada(libro = libroData, modifier = Modifier.fillMaxWidth().height(240.dp))
            Spacer(Modifier.height(12.dp))
            Text(libroData.titulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(4.dp))
            val autores = libroData.autores.joinToString { it.nombre }
            Text(autores, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(8.dp))
            Text(libroData.descripcion ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.weight(1f))
            Button(
                onClick = { onPublish?.invoke(libroData) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Publicar libro")
            }
        }
    }
}
