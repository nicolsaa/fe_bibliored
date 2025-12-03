package com.example.bibliored.api

import com.example.bibliored.model.Autor
import com.example.bibliored.model.Libro
import com.example.bibliored.model.PortadaUrl
import com.example.bibliored.network.OpenLibraryService
import com.example.bibliored.network.RetrofitProvider
import com.example.bibliored.network.ApiConfig
import com.example.bibliored.network.ConverterKind
import com.example.bibliored.network.dto.KeyRef
import com.example.bibliored.network.dto.OpenLibraryEditionDto
import com.example.bibliored.network.dto.descriptionTextOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import com.example.bibliored.util.NetworkDiagnostics

class OpenLibraryRepository(
    private val openLibraryService: OpenLibraryService,
    private val apiService: ApiService
) {

suspend fun getLibroByIsbn(
        isbn: String,
        correo: String,
        resolveAuthors: Boolean = true
    ): Result<Libro> = withContext(Dispatchers.IO) {
        runCatching {

            val dto = openLibraryService.getEditionByIsbn(isbn)

            val autores: List<Autor> = if (resolveAuthors) {
                resolveAuthors(dto.authors.orEmpty())
            } else emptyList()

            dto.toLibro(autores)
            val libroRequest: MutableMap<String, String> = mutableMapOf()
            libroRequest["isbn"] = isbn
            libroRequest["correoPropietario"] = correo

            val libroResponseDto = apiService.addBook(libroRequest)

            val body = libroResponseDto.body()
                ?: throw IllegalStateException("no book present in response body")

            val autoresApi = body.autores.map { autor -> Autor(autor.id, autor.nombre) }

            val coverId= dto.covers?.get(0)

            val libro = Libro(
                isbn10 = isbn,
                isbn13 = isbn,
                titulo = body.titulo,
                autores = autoresApi,
                descripcion = dto.description?.toString(),
                portada = buildPortada(coverId, isbn),
                workKey = "",
                editionKey = "",
                nombreUsuario = correo
                )

            libro
        }.recoverCatching { e ->
            when (e) {
                is HttpException -> {
                    throw when (e.code()) {
                        404 -> NoSuchElementException("ISBN no encontrado en OpenLibrary: $isbn")
                        else -> IllegalStateException("Error HTTP ${e.code()} en OpenLibrary", e)
                    }
                }
                is java.io.IOException -> {
                    throw IllegalStateException("Network error while accessing OpenLibrary", e)
                }
                else -> throw e
            }
        }
    }

    private suspend fun resolveAuthors(refs: List<KeyRef>): List<Autor> {
        if (refs.isEmpty()) return emptyList()
        return refs.mapNotNull { ref ->
            val raw = ref.key ?: return@mapNotNull null
            val key = raw.trimStart('/') // "/authors/OL123A" -> "authors/OL123A"
            val dto = openLibraryService.getGenericByKey(key.split("/")[1]) // devuelve name, etc.
            val nombre = dto.name ?: return@mapNotNull null
            Autor(id = 0L, nombre = nombre) // OL no da id numérico estable
        }
    }

    companion object {
        /** Construye el repo con RetrofitProvider (Moshi + logging BODY). */
        fun default(): OpenLibraryRepository {
            val configApi = ApiConfig(
                baseUrl = "http://10.0.2.2:8080",
                converter = ConverterKind.MOSHI,
                enableLogging = true, // si molestan los logs, pon false
                userAgent = "BiblioRed/1.0 (Android)"
            )

            val openLibrary  = RetrofitProvider.create<OpenLibraryService>(configApi)
            val api  = RetrofitProvider.create<ApiService>(configApi)
            return OpenLibraryRepository(openLibrary, api)
        }
    }
}

/* ---------------------- MAPPERS ---------------------- */

    private fun OpenLibraryEditionDto.toLibro(autores: List<Autor>): Libro {
        val i10 = isbn10?.firstOrNull()
        val i13 = isbn13?.firstOrNull()

        val portada = buildPortada(
            coverId = covers?.firstOrNull(),
            isbn = i13 ?: i10
        )

        return Libro(
            isbn10 = i10,
            isbn13 = i13,
            titulo = title ?: "(Sin título)",
            autores = autores,
            descripcion = description.descriptionTextOrNull(),
            portada = portada,
            workKey = works?.firstOrNull()?.key,
            editionKey = key,
            nombreUsuario = null
        )
    }

private fun buildPortada(coverId: Int?, isbn: String?): PortadaUrl? {
    //mover al backend
    fun byId(id: Int, size: String)      = "http://10.0.2.2:8080/openlibrary/getCover/${id}-${size}"
    fun byIsbn(code: String, size: String) = "http://10.0.2.2:8080/openlibrary/getIsbnCover/${code}-${size}"

    return when {
        coverId != null -> PortadaUrl(
            small  = byId(coverId, "S"),
            medium = byId(coverId, "M"),
            large  = byId(coverId, "L")
        )
        !isbn.isNullOrBlank() -> PortadaUrl(
            small  = byIsbn(isbn, "S"),
            medium = byIsbn(isbn, "M"),
            large  = byIsbn(isbn, "L")
        )
        else -> null
    }
}
