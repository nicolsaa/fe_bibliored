package com.example.bibliored.api

import com.example.bibliored.model.Libro
import com.example.bibliored.api.OpenLibraryRepository

/**
 * Convenience extension to fetch a book by ISBN using the existing repository.
 * This keeps the API surface stable while enabling UI components to request
 * a simple fetchBookByIsbn(isbn, resolveAuthors) flow.
 */
suspend fun OpenLibraryRepository.fetchBookByIsbn(
    isbn: String,
    correo: String,
    resolveAuthors: Boolean = true
): Result<Libro> = this.getLibroByIsbn(isbn, correo, resolveAuthors)
