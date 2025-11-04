package com.example.bibliored.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = BrandBrown,
    onPrimary = NeutralWhite,
    secondary = BrandBeige,
    onSecondary = NeutralBlack,

    background = NeutralWhite,  // 👈 fondo general blanco
    onBackground = NeutralBlack,
    surface = NeutralWhite,     // 👈 superficies blancas
    onSurface = NeutralBlack
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandBeige,
    onPrimary = NeutralBlack,
    secondary = BrandBrown,
    onSecondary = NeutralWhite,

    background = Color(0xFF111111),
    onBackground = NeutralWhite,
    surface = Color(0xFF1A1A1A),
    onSurface = NeutralWhite
)

/**
 * App SIEMPRE en modo claro (fondo blanco) para cumplir tu preferencia.
 * Si más adelante quieres respetar el sistema, expón un parámetro.
 */
@Composable
fun BiblioRedTheme(
    content: @Composable () -> Unit
) {
    val colors = LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = BiblioRedTypography,
        content = content
    )
}


