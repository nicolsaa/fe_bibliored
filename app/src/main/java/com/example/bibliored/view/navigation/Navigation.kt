package com.example.bibliored.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bibliored.controller.ProfileViewModel
import com.example.bibliored.controller.ProfileViewModelFactory
import com.example.bibliored.controller.messages.MessagesViewModel
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.util.SelectedBookNav
import com.example.bibliored.view.AddressScreen
import com.example.bibliored.view.BibliotecaScreen
import com.example.bibliored.view.BookDetailScreen
import com.example.bibliored.view.ConversationScreen
import com.example.bibliored.view.FormScreen
import com.example.bibliored.view.HomeScreen
import com.example.bibliored.view.MessagesScreen
import com.example.bibliored.view.ProfileScreen
import com.example.bibliored.view.SplashScreen
import com.example.bibliored.view.add.AddBookScreen
import com.example.bibliored.view.login.LoginScreen
import kotlinx.coroutines.launch

object Routes {
    const val SPLASH = "splash"
    const val LOGIN  = "login"
    const val MAIN = "main/{nombre}"
    const val ADD    = "add"
    const val FORM_SCREEN = "formScreen"
    const val BOOK_DETAIL = "bookDetail"
    const val MESSAGES = "messages"
    const val CONVERSATION = "conversation/{conversationId}?bookTitle={bookTitle}&coverUrl={coverUrl}&isNewChat={isNewChat}"
    const val PROFILE = "profile"
    const val BIBLIOTECA = "biblioteca"
    const val ADDRESS = "address" // Nueva ruta
}

@Composable
fun AppNav(modifier: Modifier = Modifier) {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val sessionPrefs = SessionPrefs(ctx)
    val scope = rememberCoroutineScope()

    // ViewModels compartidos
    val profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(ctx))
    val messagesViewModel: MessagesViewModel = viewModel()

    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH,
        modifier = modifier
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                sessionPrefs = sessionPrefs,
                onGoHome = { nombre ->
                nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onGoLogin = {
                    nav.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoggedIn = { nombre ->
                    nav.navigate("main/$nombre") {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    nav.navigate(Routes.FORM_SCREEN)
                }
            )
        }

        composable(Routes.FORM_SCREEN) {
            FormScreen(onRegistered = { nombre ->
                nav.navigate(Routes.LOGIN) {
                    popUpTo(nav.graph.id) { inclusive = true }
                }
            })
        }

        composable(Routes.MESSAGES) {
            val session by sessionPrefs.sessionFlow.collectAsState(initial = null)

            session?.let { currentSession ->
                if (currentSession.isLoggedIn) {
                    val nombre = currentSession.userName
                    MessagesScreen(
                        viewModel = messagesViewModel,
                        onConversationClick = { conversationId ->
                            nav.navigate("conversation/$conversationId?isNewChat=false")
                        },
                        onBack = {
                            nav.navigate("main/$nombre") {
                                popUpTo(Routes.MESSAGES) { inclusive = true }
                            }
                        },
                        onHomeClick = { nav.navigate("main/$nombre") },
                        onBibliotecaClick = { nav.navigate(Routes.BIBLIOTECA) },
                        onMessagesClick = { nav.navigate(Routes.MESSAGES) },
                        onProfileClick = { nav.navigate(Routes.PROFILE) }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    }
                }
            }
        }
        composable(Routes.ADD) {
            AddBookScreen(onDone = { nav.popBackStack() })
        }
        composable(
            route = Routes.CONVERSATION,
            arguments = listOf(
                navArgument("conversationId") { type = NavType.StringType },
                navArgument("bookTitle") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("coverUrl") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("isNewChat") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val isNewChat = backStackEntry.arguments?.getBoolean("isNewChat") ?: false
            ConversationScreen(
                viewModel = messagesViewModel,
                conversationId = backStackEntry.arguments?.getString("conversationId") ?: "",
                bookTitle = backStackEntry.arguments?.getString("bookTitle"),
                coverUrl = backStackEntry.arguments?.getString("coverUrl"),
                isNewChat = isNewChat,
                onBack = {
                    if (isNewChat) {
                        nav.navigate(Routes.MESSAGES) {
                            popUpTo(backStackEntry.destination.id) {
                                inclusive = true
                            }
                        }
                    } else {
                        nav.popBackStack()
                    }
                }
            )
        }
        composable(Routes.PROFILE) {
             val session by sessionPrefs.sessionFlow.collectAsState(initial = null)

            session?.let { currentSession ->
                if (currentSession.isLoggedIn) {
                    val nombre = currentSession.userName
                    ProfileScreen(
                        onBack = { nav.popBackStack() },
                        onLogout = {
                            nav.navigate(Routes.LOGIN) {
                                popUpTo(nav.graph.id) {
                                    inclusive = true
                                }
                            }
                        },
                        viewModel = profileViewModel, // Usar el viewModel compartido
                        onHomeClick = { nav.navigate("main/$nombre") },
                        onBibliotecaClick = { nav.navigate(Routes.BIBLIOTECA) },
                        onMessagesClick = { nav.navigate(Routes.MESSAGES) },
                        onProfileClick = { nav.navigate(Routes.PROFILE) },
                        onAddressClick = { nav.navigate(Routes.ADDRESS) } // Acción de navegación
                    )
                } else {
                    LaunchedEffect(Unit) {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    }
                }
            }
        }
        composable(Routes.BIBLIOTECA) {
            val session by sessionPrefs.sessionFlow.collectAsState(initial = null)

            session?.let { currentSession ->
                if (currentSession.isLoggedIn) {
                    val nombre = currentSession.userName
                    BibliotecaScreen(
                        onAddClick = { nav.navigate(Routes.ADD) },
                        onHomeClick = { nav.navigate("main/$nombre") },
                        onBibliotecaClick = { nav.navigate(Routes.BIBLIOTECA) },
                        onMessagesClick = { nav.navigate(Routes.MESSAGES) },
                        onProfileClick = { nav.navigate(Routes.PROFILE) },
                        onLibroClick = { libro ->
                            SelectedBookNav.currentLibro = libro
                            nav.navigate(Routes.BOOK_DETAIL)
                        }
                    )
                } else {
                    LaunchedEffect(Unit) {
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    }
                }
            }
        }
        composable(Routes.BOOK_DETAIL) {
            BookDetailScreen(onBack = { nav.popBackStack() })
        }
        // Composable para la pantalla de dirección
        composable(Routes.ADDRESS) {
            AddressScreen(
                viewModel = profileViewModel, // Usar el viewModel compartido
                onBack = { nav.popBackStack() }
            )
        }

        composable(
            route = Routes.MAIN,
            arguments = listOf(navArgument("nombre") { type = NavType.StringType })
        ) { backStackEntry ->
            val nombre = backStackEntry.arguments?.getString("nombre")

            if (nombre != null) {
                HomeScreen(
                    nombreCompleto = nombre,
                    sessionPrefs = sessionPrefs,
                    onLogout = {
                        scope.launch {
                            sessionPrefs.clear()
                        }
                        nav.navigate(Routes.LOGIN) {
                            popUpTo(nav.graph.id) {
                                inclusive = true
                            }
                        }
                    },
                    onAddClick = { nav.navigate(Routes.ADD) },
                    onBookContactClick = { libro, userId ->
                        scope.launch {
                            val currentSession = sessionPrefs.getCurrentSession()
                            currentSession?.let {
                                val conversationId = messagesViewModel.createConversationFromBook(
                                    otherUserId = userId,
                                    otherUserName = libro.nombreUsuario ?: "Usuario",
                                    bookTitle = libro.titulo,
                                    coverUrl = libro.portada?.medium
                                )
                                nav.navigate("conversation/$conversationId?bookTitle=${libro.titulo}&coverUrl=${libro.portada?.medium}&isNewChat=true")
                            }
                        }
                    },
                    onProfileClick = { nav.navigate(Routes.PROFILE) },
                    onBibliotecaClick = { nav.navigate(Routes.BIBLIOTECA) },
                    onMessagesClick = { nav.navigate(Routes.MESSAGES) }
                )
            }
        }
    }

}
