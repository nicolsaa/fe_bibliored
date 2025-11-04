package com.example.bibliored.network

import com.example.bibliored.network.dto.GenericKeyDto
import com.example.bibliored.network.dto.OpenLibraryEditionDto
import retrofit2.http.GET
import retrofit2.http.Path

/*Tu OpenLibraryService.kt es la interfaz de Retrofit que define las funciones (o endpoints) de la API de OpenLibrary que tu app puede consumir.
Es como un “contrato” que indica qué rutas existen, qué parámetros reciben, y qué tipo de datos devuelven.*/
interface OpenLibraryService {
    /*Retrofit genera automáticamente una implementación de esta interfaz en tiempo de ejecución (en tu OpenLibraryClient.kt, con .create(OpenLibraryService::class.java)).
    Cada función dentro de esta interfaz representa una petición HTTP a la API pública de OpenLibrary.
    💡 En resumen:
    Tú defines “qué quiero pedir”, y Retrofit se encarga de hacerlo por ti.*/
    @GET("isbn/{isbn}.json")
    suspend fun getEditionByIsbn(@Path("isbn") isbn: String): OpenLibraryEditionDto
    /*Realiza una petición GET a la URL: https://openlibrary.org/isbn/{isbn}.json
    * Por ejemplo, si isbn = "9780141036144", la URL completa será: https://openlibrary.org/isbn/9780141036144.json

    * Devuelve un objeto de tipo OpenLibraryEditionDto (una data class que representa la estructura del JSON que devuelve OpenLibrary para una “edición” de libro).*/

    @GET("{key}.json") // key tipo "authors/OL123A" o "works/OL123W" (sin slash inicial)
    suspend fun getGenericByKey(@Path("key") key: String): GenericKeyDto
    /*Llama a rutas más genéricas de la API, como:https://openlibrary.org/authors/OL123A.json
                                                  https://openlibrary.org/works/OL123W.json
     Devuelve un objeto GenericKeyDto (data class que puede representar autores, obras, etc.).*/
}