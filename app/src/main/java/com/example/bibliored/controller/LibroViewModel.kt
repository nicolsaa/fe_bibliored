package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.OpenLibraryRepository
import com.example.bibliored.model.Libro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface UiState {
    object Idle : UiState
    object Cargando : UiState
    data class Ok(val libro: Libro) : UiState
    data class Error(val msg: String) : UiState
}

class LibroViewModel(
    private val repo: OpenLibraryRepository = OpenLibraryRepository.default()
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState>(UiState.Idle)
    val estado = _estado.asStateFlow()

    fun cargarPorIsbn(isbn: String) {
        viewModelScope.launch {
            _estado.value = UiState.Cargando
            val res = repo.getLibroByIsbn(isbn, resolveAuthors = true)
            _estado.value = res.fold(
                onSuccess = { UiState.Ok(it) },
                onFailure = { UiState.Error(it.message ?: "Error desconocido") }
            )
        }
    }
}
