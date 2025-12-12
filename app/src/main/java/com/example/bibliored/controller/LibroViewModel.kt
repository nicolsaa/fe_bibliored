package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.OpenLibraryRepository
import com.example.bibliored.data.ISessionPrefs // <-- CORRECTION HERE
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Libro
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Objects

sealed interface UiState {
    object Idle : UiState
    object Cargando : UiState
    data class Ok(val libro: Libro) : UiState
    data class Error(val msg: String) : UiState
}

class LibroViewModel(
    private val repo: OpenLibraryRepository = OpenLibraryRepository.default(),
    private val sessionPrefs: ISessionPrefs // <-- CORRECTION HERE
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState>(UiState.Idle)
    val estado : StateFlow<UiState> = _estado

    private lateinit var userName: String
    private lateinit var userEmail: String

    init {
        // 👇 cada vez que cambie la sesión, lo tendrás disponible
        viewModelScope.launch {
            sessionPrefs.sessionFlow.collectLatest { session ->
                if (session.isLoggedIn) {
                    userName = session.userName
                    userEmail = session.userEmail
                }
            }
        }
    }

    fun cargarPorIsbn(isbn: String) {
        viewModelScope.launch {
            _estado.value = UiState.Cargando

            if (Objects.isNull(userName) || Objects.isNull(userEmail)) {
                throw RuntimeException("no logged in")
            }

            val user = userName ?:"desconocido"

            val res = repo.getLibroByIsbn(isbn, userEmail, resolveAuthors = true)

            _estado.value = res.fold(
                onSuccess = { UiState.Ok(it) },
                onFailure = { UiState.Error(it.message ?: "Error desconocido para $user") }
            )
        }
    }
}
