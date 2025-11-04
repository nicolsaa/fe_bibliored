package com.example.bibliored.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/*“motor universal” que tu app usa para crear clientes HTTP de Retrofit, totalmente configurables según cada API.*/

enum class ConverterKind { MOSHI, GSON, KOTLINX }
/*Define los tipos de convertidores JSON que se pueden usar:
*Moshi: ideal para Kotlin moderno (más seguro con nulls).
*Gson: el clásico (más simple, pero menos estricto).
*Kotlinx: usa kotlinx.serialization de JetBrains, útil si tus DTO usan @Serializable.  */

data class ApiConfig( // Define toda la configuración de red para una API específica.
    //👉 Esta clase te permite crear instancias personalizadas por API (por ejemplo, OpenLibrary, tu backend de login, etc.).
    val baseUrl: String,
    val converter: ConverterKind = ConverterKind.MOSHI,
    val enableLogging: Boolean = true,             // en prod puedes pasar false
    val connectTimeoutSec: Long = 15,
    val readTimeoutSec: Long = 30,
    val writeTimeoutSec: Long = 30,
    val userAgent: String = "BiblioRed/1.0 (Android)",
    val tokenProvider: (() -> String?)? = null,    // si retorna token -> agrega Authorization: Bearer
    val extraInterceptors: List<Interceptor> = emptyList()
)


object RetrofitProvider {
    /*Este es el fábrica universal de Retrofit:
    Crea un Retrofit configurado.
    Crea servicios a partir de interfaces (como OpenLibraryService).
    Te devuelve un cliente HTTP listo para usar con todas las configuraciones.
    */
    fun build(config: ApiConfig): Retrofit {
        val client = buildClient(config)
        val converterFactory = buildConverterFactory(config.converter)

        return Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(converterFactory)
            .build()
    }

    fun <T> create(service: Class<T>, config: ApiConfig): T =
        build(config).create(service)

    inline fun <reified T> create(config: ApiConfig): T =
        build(config).create(T::class.java)

    /* ----------------- Internals ----------------- */

    private fun buildClient(config: ApiConfig): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = if (config.enableLogging) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }

        val uaInterceptor = Interceptor { chain ->
            val req = chain.request().newBuilder()
                .header("User-Agent", config.userAgent)
                .build()
            chain.proceed(req)
        }

        val authInterceptor = Interceptor { chain ->
            val token = config.tokenProvider?.invoke()
            val builder = chain.request().newBuilder()
            if (!token.isNullOrBlank()) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .addInterceptor(uaInterceptor)
            .addInterceptor(authInterceptor)
            .apply { config.extraInterceptors.forEach { addInterceptor(it) } }
            .connectTimeout(config.connectTimeoutSec, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSec, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSec, TimeUnit.SECONDS)
            .build()
    }

    private fun buildConverterFactory(kind: ConverterKind): Converter.Factory {
        return when (kind) {
            ConverterKind.MOSHI -> {
                val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    // .add(YourCustomAdapter())  // si necesitas adaptadores
                    .build()
                MoshiConverterFactory.create(moshi)
            }
            ConverterKind.GSON -> GsonConverterFactory.create()
            ConverterKind.KOTLINX -> {
                val json = Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                    isLenient = true
                }
                json.asConverterFactory("application/json".toMediaType())
            }
        }
    }
}
