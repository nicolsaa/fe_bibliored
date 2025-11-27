package com.example.bibliored.network.dto.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LibroItemDto(
    val title: String,
    val authorNames: List<String>,
    val barCode: String?,
    val coverUrl: String?,
    val descripcion: String?,
    val paraIntercambio: Boolean,
    val paraRegalo: Boolean
)
