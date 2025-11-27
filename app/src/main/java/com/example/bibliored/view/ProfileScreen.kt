package com.example.bibliored.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliored.controller.ProfileViewModel
import com.example.bibliored.controller.ProfileViewModelFactory
import com.example.bibliored.view.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit, 
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModelFactory(LocalContext.current)),
    onHomeClick: () -> Unit,
    onBibliotecaClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val userProfile by viewModel.userProfile.collectAsState()

    viewModel.loadUserProfile()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentRoute = Routes.PROFILE,
                onHomeClick = onHomeClick,
                onBibliotecaClick = onBibliotecaClick,
                onMessagesClick = onMessagesClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            userProfile?.let {
                if (it.photoUrl.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera, 
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(128.dp)
                            .clickable { viewModel.updateProfilePicture() }
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(128.dp)
                            .clickable { viewModel.updateProfilePicture() }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it.fullName,
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it.email,
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.updateProfilePicture() }) {
                    Text("Cambiar foto de perfil")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { 
                    viewModel.logout()
                    onLogout()
                }) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
