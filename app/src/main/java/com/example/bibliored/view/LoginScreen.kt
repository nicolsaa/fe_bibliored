package com.example.bibliored.view.login

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bibliored.controller.AuthViewModel
import com.example.bibliored.controller.LoginState
import com.example.bibliored.data.SessionPrefs
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.example.bibliored.R
import com.example.bibliored.data.dataStore

/*Muestra el formulario de inicio de sesión.
Cuando el login es exitoso, llama a onLoggedIn(nombre) → navega al Home.
También limpia el back stack (ya no puedes volver al login).*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoggedIn: (nombreCompleto: String) -> Unit,
    onNavigateToRegister: () -> Unit = {},
    vm: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionPrefs = remember { SessionPrefs(context.dataStore) }

    val estado = vm.estado.collectAsStateWithLifecycle().value // estado actual del login
    val userExistsState by vm.userExists.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verPass by remember { mutableStateOf(false) }

    // Debounce y verificación de existencia de usuario al escribir correo
    LaunchedEffect(correo) {
        kotlinx.coroutines.delay(400)
        vm.checkUserExists(correo)
    }

    // Navegación al Home al iniciar sesión correctamente
    LaunchedEffect(estado) {
        if (estado is LoginState.Success) {
            val user = estado.usuario
            val nombre = "${user.nombre} ${user.apellido}".trim()
            sessionPrefs.setLoggedIn("user_${System.currentTimeMillis()}", nombre, correo)
            onLoggedIn(nombre)
        }
    }

    val cargando = estado is LoginState.Loading

    // Botón estático de login
    val botonLabel = "Ingresar"

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("Iniciar sesión") }) }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                Modifier.fillMaxWidth().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bibliored),
                    contentDescription = "Logo BiblioRed",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(24.dp))
                )
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (verPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (verPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                        val description = if (verPass) "Ocultar contraseña" else "Mostrar contraseña"

                        IconButton(onClick = { verPass = !verPass }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        // Si el usuario no existe, crear y luego login; si existe, hacer login
                        /*if (userExistsState == false) {
                            vm.createUserAndLogin(correo, contrasena)
                        } else {*/
                            vm.login(correo, contrasena)
                        //}
                    },
                    enabled = !cargando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (cargando) "Ingresando..." else botonLabel)
                }

                // Nueva opción de registro
                TextButton(onClick = { onNavigateToRegister() }) {
                    Text("Registrarse")
                }

                if (estado is LoginState.Error) {
                    Text(" ${estado.mensaje}", color = MaterialTheme.colorScheme.error)
                }

                TextButton(onClick = {
                    correo = ""
                    contrasena = ""
                    vm.reset()
                }) { Text("Limpiar campos") }
            }
        }
    }
}
