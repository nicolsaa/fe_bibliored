package com.example.bibliored.network.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LibroResponseDto (
    val codigoBarra: String?,
    val titulo: String,
    val autores: MutableList<AutorDto>
)