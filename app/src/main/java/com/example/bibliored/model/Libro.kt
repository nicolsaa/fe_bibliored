package com.example.bibliored.model


data class Libro(
    val isbn10: String?,          // Código ISBN-10 del libro
    val isbn13: String?,          // Código ISBN-13 (más moderno)
    val titulo: String,           // Título principal del libro
    val autores: List<Autor>,     // Lista de autores (objetos Autor)
    val descripcion: String?,     // Descripción o resumen del libro
    val portada: PortadaUrl?,     // URLs de las imágenes de portada
    val workKey: String?,         // Key del "work" (obra principal)
    val editionKey: String?,       // Key específica de la edición (/books/OL...)
    val nombreUsuario: String?,
    val paraIntercambio: Boolean = false,
    val paraRegalo: Boolean = false
)