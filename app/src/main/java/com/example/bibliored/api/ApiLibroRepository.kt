package com.example.bibliored.api

import com.example.bibliored.model.Libro
import com.example.bibliored.model.Autor
import com.example.bibliored.model.PortadaUrl
import com.example.bibliored.network.ApiConfig
import com.example.bibliored.network.ConverterKind
import com.example.bibliored.network.RetrofitProvider
import retrofit2.Response
import java.io.IOException
import java.util.Collections
import java.util.Optional

class ApiLibroRepository(private val api: ApiService) : LibroRepository {

    override suspend fun getLibroPorCorreo(correo: String): Result<List<Libro>> {
        return try {
            val httpResponse: Response<Map<String, List<Map<String, String>>>> =
                this.api.getBookByEmail(correo)

            if (httpResponse.isSuccessful) {
                val body = httpResponse.body()

                if (body != null) {
                    // Se espera una clave "libros" con una lista de items
                    val librosRaw: List<Map<String, String>> = (body["libros"] ?: emptyList())

                    val libros = librosRaw.map { item ->
                        val title = item["title"] ?: ""

                        // authorNames llega como String que puede representar una lista en formato JSON/CSV
                        val authorsRaw = item["authorNames"] ?: ""
                        val authorNames = parseAuthorsFromString(authorsRaw)
                        val autores = authorNames.map { Autor(nombre = it) }

                        Libro(
                            isbn10 = item["isbn10"],
                            isbn13 = item["isbn13"],
                            titulo = title,
                            autores = autores,
                            descripcion = item["descripcion"] ?: item["description"],
                            portada = PortadaUrl(null, null, null),
                            workKey = item["workKey"],
                            editionKey = item["editionKey"]
                        )
                    }
                    Result.success(libros)
                } else {
                    Result.success(Collections.emptyList<Libro>())
                }

            } else {
                val errorContent = httpResponse.errorBody()?.string()
                Result.failure(IOException("error al obtener libros: $errorContent"))
            }
        } catch (t: Throwable) {
            // Proporcionar contexto adicional para errores de JSON
            if (t is com.squareup.moshi.JsonDataException) {
                Result.failure(IOException("Malformed JSON in response from registrarUsuario: ${t.message}"))
            } else {
                Result.failure(t)
            }
        }
    }

    private fun parseAuthorsFromString(input: String): List<String> {
        var s = input.trim()
        if (s.startsWith("[") && s.endsWith("]")) {
            s = s.substring(1, s.length - 1)
        }
        if (s.isBlank()) return emptyList()
        // Eliminar comillas alrededor de cada nombre si existen
        return s.split(",").map { it.trim().trim('"') }.filter { it.isNotEmpty() }
    }

    companion object {
        fun default(): ApiLibroRepository {
            val baseUrl = "http://10.0.2.2:8080/"
            val config = ApiConfig(
                baseUrl = baseUrl,
                converter = ConverterKind.MOSHI,
                enableLogging = true,
                userAgent = "BiblioRed/1.0 (Android)"
            )
            val api = RetrofitProvider.create<ApiService>(config)
            return ApiLibroRepository(api)
        }
    }
}
