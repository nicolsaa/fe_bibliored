package com.example.bibliored.model

import java.time.LocalDateTime

data class Intercambio(
    val id: Long,
    val libro: Libro,
    val solicitante: Usuario,
    val destinatario: Usuario,
    val estado: Estado,
    val fechaSolicitud: LocalDateTime,
    val fechaAceptacion: LocalDateTime?
) {
    enum class Estado {
        SOLICITADO,
        ACEPTADO,
        RECHAZADO
    }
}
