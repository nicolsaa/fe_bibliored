package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.bibliored.model.Libro

class LibraryViewModel : ViewModel() {
    private val _libros = MutableStateFlow<List<Libro>>(emptyList())
    val libros: StateFlow<List<Libro>> = _libros.asStateFlow()

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
