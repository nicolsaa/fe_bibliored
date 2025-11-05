package com.example.bibliored.api

import com.example.bibliored.network.dto.RegistroUsuarioDto
import com.example.bibliored.network.dto.response.LibroResponseDto

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("usuarios/registrar")
    suspend fun registrarUsuario(@Body body: RegistroUsuarioDto, @Header("Cookie") cookie: String? = null): Response<Map<String, String>>

    @POST("libros/add-libro")
    suspend fun addBook(@Body body: Map<String, String>): Response<LibroResponseDto>

    @GET("/libros/email/{correo}")
    suspend fun getBookByEmail(@Path("correo") correo: String): Response<Map<String, List<Map<String, String>>>>
}
