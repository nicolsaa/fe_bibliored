package com.example.bibliored.api

import com.example.bibliored.model.Usuario
import kotlinx.coroutines.delay

interface AuthRepository {
    suspend fun login(correo: String, contrasena: String): Result<Usuario>
    suspend fun userExists(correo: String): Boolean
    suspend fun createUser(nombre: String, apellido: String, correo: String, contrasena: String): Result<Usuario>
    suspend fun logout()
}
