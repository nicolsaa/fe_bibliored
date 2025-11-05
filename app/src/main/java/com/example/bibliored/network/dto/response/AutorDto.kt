package com.example.bibliored.network.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AutorDto (
    val id: Long,
    val nombre: String
)