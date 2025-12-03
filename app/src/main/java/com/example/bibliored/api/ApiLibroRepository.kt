package com.example.bibliored.api

import com.example.bibliored.model.Libro
import com.example.bibliored.model.Autor
import com.example.bibliored.model.PortadaUrl
import com.example.bibliored.network.dto.response.LibroItemDto
import com.example.bibliored.network.RetrofitProvider
import com.example.bibliored.network.ApiConfig
import com.example.bibliored.network.ConverterKind
import com.example.bibliored.network.dto.response.LibroUserResponseDto
import retrofit2.Response
import java.io.IOException

class ApiLibroRepository(private val api: ApiService) : LibroRepository {

    override suspend fun getLibroPorCorreo(correo: String): Result<List<Libro>> {
        return try {
            val httpResponse: Response<LibroUserResponseDto> =
                this.api.getBookByEmail(correo)

            if (httpResponse.isSuccessful) {
                val body = httpResponse.body()

                if (body != null) {
                    // body.libros es una lista de LibroItemDto
                    val librosRaw: List<LibroItemDto> = body.libros

                    val libros = librosRaw.map { item ->
                        val portadaUrl = item.coverUrl?.let { PortadaUrl(small = it, medium = it, large = it) }
                        Libro(
                            isbn10 = null,
                            isbn13 = item.barCode,
                            titulo = item.title,
                            autores = item.authorNames.map { Autor(nombre = it) },
                            descripcion = item.descripcion,
                            portada = portadaUrl,
                            workKey = null,
                            editionKey = null,
                            nombreUsuario = correo
                        )
                    }
                    Result.success(libros)
                } else {
                    Result.success(emptyList())
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

    override suspend fun getLibros(): Result<List<Libro>> {
        return try {
            val httpResponse: Response<LibroUserResponseDto> = this.api.getBooks()

            if (httpResponse.isSuccessful) {
                val body = httpResponse.body()
                if (body != null) {
                    val librosRaw: List<LibroItemDto> = body.libros
                    val libros = librosRaw.map { item ->
                        val portadaUrl = item.coverUrl?.let { PortadaUrl(small = it, medium = it, large = it) }
                        Libro(
                            isbn10 = null,
                            isbn13 = item.barCode,
                            titulo = item.title,
                            autores = item.authorNames.map { Autor(nombre = it) },
                            descripcion = item.descripcion,
                            portada = portadaUrl,
                            workKey = null,
                            editionKey = null,
                            nombreUsuario = null // No hay nombre de usuario en este endpoint
                        )
                    }
                    Result.success(libros)
                } else {
                    Result.success(emptyList())
                }
            } else {
                val errorContent = httpResponse.errorBody()?.string()
                Result.failure(IOException("error al obtener todos los libros: $errorContent"))
            }
        } catch (t: Throwable) {
            Result.failure(t)
        }
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