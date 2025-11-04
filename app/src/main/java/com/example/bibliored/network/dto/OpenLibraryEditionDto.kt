package com.example.bibliored.network.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/*Data Transfer Object (DTO) que describe la estructura de datos que devuelve OpenLibrary cuando consultas:https://openlibrary.org/isbn/{ISBN}.json
*
*Por ejemplo, si haces un GET a
https://openlibrary.org/isbn/9780141036144.json
te devuelve un JSON similar a:
* {
  "key": "/books/OL24226808M",
  "title": "The God Delusion",
  "isbn_10": ["0141036141"],
  "isbn_13": ["9780141036144"],
  "description": "A book by Richard Dawkins",
  "covers": [123456],
  "works": [{"key": "/works/OL82563W"}],
  "authors": [{"key": "/authors/OL12345A"}]
}
 Tu DTO permite convertir eso directamente a un objeto Kotlin.*/
@JsonClass(generateAdapter = true) // Le dice a Moshi que genere automáticamente el adaptador de conversión JSON ↔ Kotlin.
data class OpenLibraryEditionDto(
    val key: String?,
    val title: String?,
    @Json(name = "isbn_10") val isbn10: List<String>?, /*Mapea un campo del JSON con un nombre diferente al de la propiedad Kotlin.
                                                        (porque Kotlin no acepta variables con _ en nombre de parámetro limpio).*/
    @Json(name = "isbn_13") val isbn13: List<String>?,
    val description: Any?,          /* Puede venir como String o como un objeto con valor, por eso es Any?.
    Por ejemplo: "description": "Short text"
                 "description": { "value": "Text largo" }
    */
    val covers: List<Int>?,
    val works: List<KeyRef>?,
    val authors: List<KeyRef>?
)

@JsonClass(generateAdapter = true)
data class KeyRef(val key: String?)
/*Sirve para mapear pequeñas referencias del tipo: "authors": [{"key": "/authors/OL12345A"}]
* De esta manera, tu app sabe que: El autor se identifica por /authors/OL12345A
* Y tu repositorio luego puede usar esas keys para hacer nuevas peticiones:*/

/*
* Este DTO no se usa directamente en la UI; se utiliza dentro de tu OpenLibraryRepository: val dto = api.getEditionByIsbn(isbn)

Después el repositorio transforma el DTO → modelo de dominio (Libro) con algo así: dto.toLibro(autores)

Donde arma un objeto de negocio más limpio, con: Título, ISBN, Autores ya resueltos (como texto), Portada con URL completa*/
