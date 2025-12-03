package com.example.bibliored.network.dto

import com.squareup.moshi.Json

data class UsuarioDto(
    @Json(name = "id") val id: Long,
    @Json(name = "nombre") val nombre: String,
    @Json(name = "apellido") val apellido: String,
    @Json(name = "correo") val correo: String,
    @Json(name = "contrasena") val contrasena: String
)