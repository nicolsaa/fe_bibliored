package com.example.bibliored.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.view.BookDetailScreen
import com.example.bibliored.view.FormScreen
import com.example.bibliored.view.HomeScreen
import com.example.bibliored.view.SplashScreen
import com.example.bibliored.view.add.AddBookScreen
import com.example.bibliored.view.login.LoginScreen

/*👉 Es el “mapa de rutas” de toda la app.
Cada composable() dentro del NavHost representa una pantalla, y el NavController se encarga de moverse entre ellas (como si fueran páginas). 

*/

/*Este objeto simplemente centraliza los nombres de las rutas.
Sirve para que no tengas strings duplicados y sea más fácil mantenerlas.

"splash" → Pantalla inicial

"login" → Pantalla de inicio de sesión

"home/{nombre}" → Pantalla principal con un argumento (nombre)

"add" → Pantalla para agregar/escanear libros

FormScreen → pantalla para registrar un nuevo usuario*/

object Routes {
    const val Splash = "splash"
    const val Login  = "login"
    const val Home   = "home/{nombre}"
    const val Add    = "add"
    const val FormScreen = "formScreen"
    const val BookDetail = "bookDetail"
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val sessionPrefs = SessionPrefs(ctx)

    /*AppNav
    - rememberNavController() crea el controlador de navegación.
    Es lo que usas para moverte entre pantallas con nav.navigate("ruta"). 
    - LocalContext.current obtiene el contexto actual de Android.
    - SessionPrefs(ctx) inicializa la clase que maneja el DataStore de sesión.
    (la usas para saber si el usuario está logeado).*/

    NavHost(
        /*El NavHost contiene todas las pantallas (composable) que puedes visitar.
        - startDestination define la pantalla inicial → en tu caso, el Splash.*/
        navController = nav,
        startDestination = Routes.Splash,
        modifier = modifier
    ) {
        /*Muestra la pantalla Splash al abrir la app.
        Le pasa sessionPrefs para leer si hay una sesión guardada.
        Si el usuario ya está logeado, navega directo a home/{nombre}.
        Si no está logeado, va al login.*/
        composable(Routes.Splash) {
            SplashScreen(
                sessionPrefs = sessionPrefs,
                onGoHome = { nombre ->
                    nav.navigate("home/$nombre") {
                        popUpTo(Routes.Splash) { inclusive = true } /*popUpTo(... { inclusive = true }) borra la pantalla anterior del stack,
                                                                    para que no puedas volver atrás con el botón de “atrás”.*/
                    }
                },
                onGoLogin = {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Splash) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.Login) {
            /*Muestra el formulario de inicio de sesión.
            Cuando el login es exitoso, llama a onLoggedIn(nombre) → navega al Home.
            También limpia el back stack (ya no puedes volver al login).*/
            LoginScreen(
                onLoggedIn = { nombre ->
                    // ✅ CORRECTO: Con parámetro
                    nav.navigate("home/$nombre") {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    nav.navigate(Routes.FormScreen)
                }
            )
        }


        composable(
            route = Routes.Home,
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre") ?: "Usuario"
            HomeScreen(
                nombreCompleto = nombre,
                sessionPrefs = sessionPrefs,
                onLogout = {
                    nav.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                onAddClick = { nav.navigate(Routes.Add) }   // ⬅️ navega a Agregar/Escanear
            )
        }

        /*Pantalla para agregar/escanear libros.
        Cuando termina (onDone()), usa nav.popBackStack() para volver atrás al HomeScreen.*/
        composable(Routes.Add) {
            AddBookScreen(
                onDone = { nav.popBackStack() },
                openDetail = { libro ->
                    com.example.bibliored.util.SelectedBookNav.currentLibro = libro
                    nav.navigate(Routes.BookDetail)
                }
            )
        }

        /* Pantalla de registro de nuevo usuario */
        composable(Routes.FormScreen) {
            FormScreen(
                onRegistered = { nombreCompleto ->
                    nav.navigate("home/$nombreCompleto") {
                        popUpTo(Routes.FormScreen) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.BookDetail) {
            BookDetailScreen(onBack = { com.example.bibliored.util.SelectedBookNav.currentLibro = null; nav.popBackStack() }, libro = null)
        }
    }
}
