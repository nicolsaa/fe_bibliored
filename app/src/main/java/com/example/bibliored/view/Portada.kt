package com.example.bibliored.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bibliored.model.Libro

/*define un Composable reutilizable que muestra la imagen de portada de un libro (si existe), o un ícono por defecto si no hay imagen disponible.*/
@Composable
fun Portada( //Esta función dibuja la portada de un libro.
    libro: Libro, //libro: objeto de tipo Libro, que contiene las URLs de las portadas (small, medium, large).
    modifier: Modifier = Modifier.size(140.dp) // modifier: permite personalizar el tamaño o estilo desde fuera del componente. Por defecto, la portada mide 140.dp.
    //Este patrón hace que Portada() sea reutilizable en cualquier parte (por ejemplo, en el HomeScreen).
) {
    val url = libro.portada?.large ?: libro.portada?.medium ?: libro.portada?.small
    /*Usa el operador Elvis (?:) para elegir la mejor opción disponible:
    Si existe una imagen grande (large), la usa.
    Si no, intenta con media (medium).
    Si tampoco hay, usa pequeña (small).
    Si todas son null, la variable url quedará null.
    Esto asegura que siempre se use la mejor resolución posible.*/

    if (url != null) {
        AsyncImage(
            /*Usa AsyncImage de la librería Coil (Kotlin Image Loader) para:
            Cargar imágenes desde Internet de forma eficiente y asíncrona..*/
            model = ImageRequest.Builder(LocalContext.current) // Usa el contexto de la app (necesario para Coil).
                .data(url) // Asigna la URL (data(url)).
                .crossfade(true) // Mostrar una animación de fundido suave (crossfade(true)).
                .build(), // Construye la configuración final (build()).
            contentDescription = "Portada de ${libro.titulo}",
            modifier = modifier,
            contentScale = ContentScale.Crop //Cargar imágenes desde Internet de forma eficiente y asíncrona.
        ) //Resultado: muestra la portada real del libro cargada desde OpenLibrary.
    } else {
        Box(
            /*Muestra una Box (un contenedor cuadrado):
            Con un fondo gris claro (#EAEAEA).
            Con el contenido centrado (contentAlignment = Alignment.Center).*/
            modifier = modifier.background(Color(0xFFEAEAEA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.MenuBook, //Dentro de la caja, se dibuja un ícono de libro (MenuBook) en gris oscuro (#616161).
                contentDescription = "Sin portada",
                tint = Color(0xFF616161)
            )
        }
    }
}
