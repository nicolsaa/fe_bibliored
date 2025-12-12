package com.example.bibliored.view

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bibliored.R
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Session
import com.example.bibliored.ui.theme.BrandBeige
import kotlinx.coroutines.delay

/*La SplashScreen es la primera pantalla que se muestra al abrir la aplicación.
Su función es verificar si el usuario ya tiene una sesión activa o no, antes de decidir si debe ir al Login
 o directamente al Home.

 Lee la sesión guardada desde SessionPrefs.sessionFlow.
Esa clase usa DataStore para guardar si el usuario inició sesión, su id, nombre y correo.

Observa los datos con collectAsStateWithLifecycle().
Apenas obtiene el flujo de sesión (Flow<Sesion>), sabe si hay un usuario activo o no.

Decide a dónde navegar:
✅ Si hay sesión → te lleva directo al Home
❌ Si no hay sesión → te lleva al Login*/

@Composable
fun SplashScreen(
    sessionPrefs: SessionPrefs,
    onGoHome: (name: String) -> Unit,
    onGoLogin: () -> Unit
) {
    val session = sessionPrefs.sessionFlow.collectAsStateWithLifecycle(
        initialValue = Session(isLoggedIn = false)
    ).value

    var start by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (start) 1f else 0.85f,
        animationSpec = tween(700, easing = FastOutSlowInEasing), label = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (start) 1f else 0f,
        animationSpec = tween(700), label = "alpha"
    )

    var navigated by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        start = true
        delay(850)

        if (!navigated) {
            // ✅ VERIFICACIÓN MEJORADA con manejo seguro del nombre
            if (session.isLoggedIn) {
                val user_name = session.userName

                // ✅ Verificación adicional para asegurar que el nombre no esté vacío
                if (user_name.isNotBlank()) {
                    onGoHome(user_name)
                } else {
                    // Si el nombre está vacío, vamos al login
                    onGoLogin()
                }
            } else {
                onGoLogin()
            }
            navigated = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrandBeige),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.scale(scale).alpha(alpha)
        ) {
            Image(
                painter = painterResource(id = R.drawable.bibliored),
                contentDescription = "Logo BiblioRed",
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(24.dp))
            )
            CircularProgressIndicator()

            // ✅ Opcional: Texto de depuración para ver qué está pasando
            if (session.isLoggedIn) {
                Text(
                    text = "Bienvenido",
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
    }
}
