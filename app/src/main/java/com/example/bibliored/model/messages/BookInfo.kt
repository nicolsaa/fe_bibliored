package com.example.bibliored.model.messages

data class BookInfo(
    val title: String,
    val coverUrl: String?,
    val bookId: String? = null
)
