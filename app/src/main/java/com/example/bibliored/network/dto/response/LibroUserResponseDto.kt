package com.example.bibliored.network.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LibroUserResponseDto(
    val libros: List<LibroItemDto>
)