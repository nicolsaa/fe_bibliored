package com.example.bibliored.view.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.bibliored.controller.LibraryViewModel
import com.example.bibliored.controller.LibroViewModel
import com.example.bibliored.controller.LoginState
import com.example.bibliored.controller.UiState
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Libro
import com.example.bibliored.view.camera.QrScannerScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    var mostrarScanner by remember { mutableStateOf(false) }

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
                    // We don't save automatically anymore
                    // guardado = true
                }
                is UiState.Error -> {
                    loading = false
                    error = state.msg
                }
                else -> { /* Idle */ }
            }
        }
    }

    if (mostrarScanner) {
        QrScannerScreen(
            onQrScanned = { scannedIsbn ->
                mostrarScanner = false
                isbn = scannedIsbn
                // Automatically trigger the search
                if (scannedIsbn.isNotBlank()) {
                    scope.launch {
                        loading = true
                        error = null
                        try {
                            vm.cargarPorIsbn(scannedIsbn)
                        } catch (e: Exception) {
                            error = e.message ?: "Error desconocido"
                        } finally {
                            loading = false
                        }
                    }
                }
            },
            onClose = {
                mostrarScanner = false
            }
        )
    } else {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Agregar libro por ISBN")

                OutlinedTextField(
                    value = isbn,
                    onValueChange = { isbn = it },
                    label = { Text("ISBN") },
                    singleLine = true,
                    modifier = Modifier.width(320.dp),
                    trailingIcon = {
                        IconButton(onClick = { mostrarScanner = true }) {
                            Icon(
                                Icons.Default.QrCodeScanner,
                                contentDescription = "Escanear código de barras"
                            )
                        }
                    }
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
                        AsyncImage(
                            model = url,
                            contentDescription = libro.titulo,
                            modifier = Modifier.size(180.dp)
                        )
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

                    Button(onClick = {
                        val libroActualizado = libro.copy()
                        scope.launch {
                            libraryVm.add(libroActualizado)
                            guardado = true
                        }
                        onDone()
                    }) {
                        Text("Guardar libro")
                    }
                }

                error?.let { msg ->
                    Text(text = msg, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(16.dp))

                if (guardado) {
                    Text("Libro agregado a la biblioteca", color = MaterialTheme.colorScheme.primary)
                }

                OutlinedButton(onClick = onDone) { Text("Volver atrás") }
            }
        }
    }
}