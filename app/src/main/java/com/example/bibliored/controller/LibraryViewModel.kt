package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.ApiLibroRepository
import com.example.bibliored.api.LibroRepository
import com.example.bibliored.data.SessionPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.bibliored.model.Libro
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

sealed interface LibraryState {
    object Idle : LibraryState
    object Loading : LibraryState
    data class Success(val libros: List<Libro>) : LibraryState
    data class Error(val mensaje: String) : LibraryState
}
class LibraryViewModel(
    private val libroRepository: LibroRepository = ApiLibroRepository.default(),
    private val sessionPrefs: SessionPrefs
) : ViewModel() {

    private val _estado: MutableStateFlow<LibraryState> = MutableStateFlow(LibraryState.Idle)
    val estado: StateFlow<LibraryState> = _estado.asStateFlow()
    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros.asStateFlow()

    private val _librosParaIntercambio = MutableStateFlow<List<Libro>>(emptyList())
    val librosParaIntercambio: StateFlow<List<Libro>> = _librosParaIntercambio.asStateFlow()

    private lateinit var userName: String
    private lateinit var userEmail: String

    init {
        viewModelScope.launch {
            sessionPrefs.sessionFlow.collectLatest { session ->
                if (session.isLoggedIn) {
                    userName = session.userName
                    userEmail = session.userEmail
                }
            }
        }
    }

    suspend fun getLibros(): List<Libro> {
        // Espera a que userEmail esté inicializado
        if (!this::userEmail.isInitialized) {
            while (!this::userEmail.isInitialized) {
                delay(50)
            }
        }
        val librosList = libroRepository.getLibroPorCorreo(userEmail).getOrThrow()
        _libros.value = librosList
        return librosList
    }

    suspend fun getLibrosParaIntercambio() {
        val result = libroRepository.getLibros()
        result.fold(
            onSuccess = { libros ->
                _librosParaIntercambio.value = libros.filter { it.paraIntercambio }
            },
            onFailure = { 
                // Handle error
            }
        )
    }

    fun add(libro: Libro) {
        // Evitar duplicados por ISBN (si disponible)
        val exists = _libros.value.any { it.isbn10 == libro.isbn10 || it.isbn13 == libro.isbn13 }
        if (!exists) {
            _libros.value = _libros.value + libro
        }
    }

    fun remove(libro: Libro) {
        _libros.value = _libros.value - libro
    }

    fun clear() {
        _libros.value = emptyList()
    }
}
