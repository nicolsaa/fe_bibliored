package com.example.bibliored.api

import com.example.bibliored.model.Libro

interface LibroRepository {

    suspend fun getLibroPorCorreo(correo: String): Result<List<Libro>>
}