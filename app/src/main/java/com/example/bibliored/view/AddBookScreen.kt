package com.example.bibliored.view.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliored.controller.LibraryViewModel
import com.example.bibliored.model.Autor
import com.example.bibliored.model.Libro
import com.example.bibliored.model.PortadaUrl
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collect
import com.example.bibliored.controller.UiState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.example.bibliored.controller.LibroViewModel
import com.example.bibliored.controller.LoginState
import com.example.bibliored.data.SessionPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/*pantalla temporal (demo) para agregar libros a tu biblioteca.
Este nuevo flujo permite ingresar un ISBN, buscar automáticamente y rellenar
título, descripcion, portada y autor. Luego se guarda en memoria y se abre el detalle.*/

@Composable
fun AddBookScreen(
    onDone: () -> Unit, // onDone: callback que se ejecuta cuando el usuario termina (por ejemplo, para volver al Home).
    //libraryVm: LibraryViewModel = viewModel(), // tu ViewModel (LibraryViewModel), encargado de manejar la lista de libros en la biblioteca.
    openDetail: ((Libro) -> Unit)? = null // opcional: abrir detalle del libro después de guardar
) {
    var isbn by remember { mutableStateOf("") }
    var libroPreview by remember { mutableStateOf<Libro?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var guardado by remember { mutableStateOf(false) }
    val _estado: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Idle)
    val estado: StateFlow<LoginState> = _estado.asStateFlow()
    val ctx = LocalContext.current
    val sessionPrefs = remember(ctx) { SessionPrefs(ctx) }
    val vm = remember { LibroViewModel(sessionPrefs = sessionPrefs) }
    val libraryVm = remember { LibraryViewModel(sessionPrefs = sessionPrefs) }
    val scope = rememberCoroutineScope()

    // Observe ViewModel UI state to trigger navigation and show feedback
    LaunchedEffect(Unit) {
        vm.estado.collect { state ->
            when (state) {
                is UiState.Cargando -> loading = true
                is UiState.Ok -> {
                    loading = false
                    libroPreview = state.libro
                    guardado = true
                }
                is UiState.Error -> {
                    loading = false
                    error = state.msg
                }
                else -> { /* Idle */ }
            }
        }
    }

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("Agregar libro por ISBN")

            OutlinedTextField(
                value = isbn,
                onValueChange = { isbn = it },
                label = { Text("ISBN") },
                singleLine = true,
                modifier = Modifier.width(320.dp)
            )

            Button(
                onClick = {
                    if (isbn.isBlank()) return@Button
                    scope.launch {
                        loading = true
                        error = null
                        try {
                            vm.cargarPorIsbn(isbn)
                        } catch (e: Exception) {
                            error = e.message ?: "Error desconocido"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                }
                Text("Buscar")
            }

            libroPreview?.let { libro ->
                // Portada
                libro.portada?.medium?.let { url ->
                    AsyncImage(model = url, contentDescription = libro.titulo, modifier = Modifier.size(180.dp))
                    Spacer(Modifier.height(8.dp))
                }
                // Título (lectura)
                OutlinedTextField(
                    value = libro.titulo,
                    onValueChange = { /* read-only */ },
                    label = { Text("Título") },
                    readOnly = true,
                    modifier = Modifier.width(320.dp)
                )
                // Descripción
                OutlinedTextField(
                    value = libro.descripcion ?: "",
                    onValueChange = { /* read-only */ },
                    label = { Text("Descripción") },
                    readOnly = true,
                    modifier = Modifier.width(320.dp).height(100.dp)
                )
                // Autores
                val autoresStr = libro.autores.joinToString { it.nombre }
                OutlinedTextField(
                    value = autoresStr,
                    onValueChange = { /* read-only */ },
                    label = { Text("Autor") },
                    readOnly = true,
                    modifier = Modifier.width(320.dp)
                )

                Spacer(Modifier.height(8.dp))

                Button(onClick = { onDone() }) {
                    Text("volver a la biblioteca")
                }
            }

            error?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(16.dp))

            if (guardado) {
                Text("Libro agregado a la biblioteca", color = MaterialTheme.colorScheme.primary)
            }

            OutlinedButton(onClick = onDone) { Text("Cancelar") }
        }
    }
}
