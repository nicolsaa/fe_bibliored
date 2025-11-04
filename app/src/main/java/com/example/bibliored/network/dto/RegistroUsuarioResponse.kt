package com.example.bibliored.network.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegistroUsuarioResponse(
    val id: Long?,
    val nombre: String?
)
