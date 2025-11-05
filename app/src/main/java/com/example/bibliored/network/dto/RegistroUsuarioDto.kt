package com.example.bibliored.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistroUsuarioDto(
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String
)
