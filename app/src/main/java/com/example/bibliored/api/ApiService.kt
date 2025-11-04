package com.example.bibliored.api

import com.example.bibliored.network.dto.RegistroUsuarioDto
import com.example.bibliored.network.dto.RegistroUsuarioResponse
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("usuarios/registrar")
suspend fun registrarUsuario(@Body body: RegistroUsuarioDto, @Header("Cookie") cookie: String? = null): Response<Map<String, String>>

    @POST("libros/add-libro")
    suspend fun addLibro(@Body body: RequestBody): ResponseBody
}
