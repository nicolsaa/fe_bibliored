package com.example.bibliored.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistroUsuarioDto(
    val correo: String,
    val contrasena: String
)
