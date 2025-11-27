package com.example.bibliored.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bibliored.view.navigation.Routes

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == Routes.MAIN,
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Book, contentDescription = "Biblioteca") },
            label = { Text("Biblioteca") },
            selected = currentRoute == Routes.BIBLIOTECA,
            onClick = onBibliotecaClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Message, contentDescription = "Mensajes") },
            label = { Text("Mensajes") },
            selected = currentRoute == Routes.MESSAGES,
            onClick = onMessagesClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Perfil") },
            selected = currentRoute == Routes.PROFILE,
            onClick = onProfileClick
        )
    }
}
