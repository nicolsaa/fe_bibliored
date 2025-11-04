package com.example.bibliored.model

data class Session(
    val isLoggedIn: Boolean = false,
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = ""
)