package com.example.bibliored.model

data class Usuario(
    var id: Long = 0,
    var nombre: String,
    var apellido: String,
    var correo: String,
    var contrasena: String
)
