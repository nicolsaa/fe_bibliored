package com.example.bibliored.api

import com.example.bibliored.model.Usuario
import com.example.bibliored.network.RetrofitProvider
import com.example.bibliored.network.ApiConfig
import com.example.bibliored.network.ConverterKind
import com.example.bibliored.network.dto.LoginDto
import com.example.bibliored.network.dto.RegistroUsuarioDto
import retrofit2.Response
import java.io.IOException

class ApiAuthRepository(private val api: ApiService, private val cookieHeader: String? = null) : AuthRepository {

    override suspend fun login(correo: String, contrasena: String): Result<Usuario> {
        return try {
            val loginDto = LoginDto(correo, contrasena)
            val response = api.login(loginDto)
            if (response.isSuccessful) {
                val usuarioDto = response.body()!!
                val usuario = Usuario(
                    id = usuarioDto.id,
                    nombre = usuarioDto.nombre,
                    apellido = usuarioDto.apellido,
                    correo = usuarioDto.correo,
                    contrasena = usuarioDto.contrasena
                )
                Result.success(usuario)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Result.failure(IOException(errorBody))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun userExists(correo: String): Boolean {
        // No hay endpoint para verificar existencia de usuario en la API actual.
        // Devuelve false por seguridad; puede ampliarse si se añade un endpoint.
        return false
    }

    override suspend fun createUser(nombre: String, apellido: String, correo: String, contrasena: String): Result<Usuario> {
        return try {
            val payload = RegistroUsuarioDto(nombre = nombre, apellido = apellido, correo = correo, contrasena = contrasena)
            val httpResponse: Response<Map<String, String>> = api.registrarUsuario(payload, cookieHeader)

            if (httpResponse.isSuccessful) {
                val usuario = Usuario(
                    nombre = nombre,
                    apellido = apellido,
                    correo = correo,
                    contrasena = contrasena
                )
                Result.success(usuario)
            } else {
                val errorContent = httpResponse.errorBody()?.string()
                Result.failure(IOException("Error al registrar usuario: $errorContent"))
            }
        } catch (t: Throwable) {
            // Provide more context for JSON parsing errors
            if (t is com.squareup.moshi.JsonDataException) {
                Result.failure(IOException("Malformed JSON in response from registrarUsuario: ${t.message}"))
            } else {
                Result.failure(t)
            }
        }
    }

    override suspend fun logout() {
        // Por ahora, esta función no hará nada.
        // La lógica real de cierre de sesión se implementará más adelante.
    }

    companion object {
        // Construye la instancia usando RetrofitProvider y ApiService
        // baseIsLocalEmulator = true apunta a 10.0.2.2 para emulador Android
        fun default(baseIsLocalEmulator: Boolean = true): ApiAuthRepository {
            val baseUrl = "http://10.0.2.2:8080/"
            val config = ApiConfig(
                baseUrl = baseUrl,
                converter = ConverterKind.MOSHI,
                enableLogging = true,
                userAgent = "BiblioRed/1.0 (Android)"
            )
            val api = RetrofitProvider.create<ApiService>(config)
            return ApiAuthRepository(api)
        }
    }
}
