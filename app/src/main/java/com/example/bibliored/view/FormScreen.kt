package com.example.bibliored.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.bibliored.controller.AuthViewModel
import com.example.bibliored.controller.LoginState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.bibliored.controller.RegisterState
import com.example.bibliored.controller.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    onRegistered: (nombreCompleto: String) -> Unit,
    vm: RegisterViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var verPass by remember { mutableStateOf(false) }
    var verPassConf by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    val estado = vm.estado.collectAsStateWithLifecycle().value
    val cargando = estado is RegisterState.Loading

    // Observa resultados de login/registro
    LaunchedEffect(estado) {
        when (estado) {
            is RegisterState.Success -> {
                val u = estado.usuario
                val nombreCompleto = "${u.nombre} ${u.apellido}".trim()
                onRegistered(nombreCompleto)
            }
            is RegisterState.Error -> {
                error = estado.mensaje
            }
            else -> {}
        }
    }

    androidx.compose.material3.Surface {
        androidx.compose.material3.Scaffold(
            topBar = { CenterAlignedTopAppBar(title = { Text("Registro") }) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = apellido,
                        onValueChange = { apellido = it },
                        label = { Text("Apellido") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
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
                    OutlinedTextField(
                        value = confirmar,
                        onValueChange = { confirmar = it },
                        label = { Text("Confirmar contraseña") },
                        singleLine = true,
                        visualTransformation = if (verPassConf) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (verPassConf) Icons.Filled.VisibilityOff else Icons.Filled.Visibility
                            val description = if (verPassConf) "Ocultar contraseña" else "Mostrar contraseña"

                            IconButton(onClick = { verPassConf = !verPassConf }) {
                                Icon(imageVector = image, contentDescription = description)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (error.isNotEmpty()) {
                        Text(error, color = MaterialTheme.colorScheme.error)
                    }

                    Button(
                        onClick = {
                            // Validaciones básicas
                            if (nombre.isBlank()) {
                                error = "Nombre es obligatorio";
                                return@Button }
                            if (apellido.isBlank()) {
                                error = "Apellido es obligatorio"
                                return@Button
                            }
                            val emailOk = correo.contains("@") && correo.contains(".")
                            if (!emailOk) {
                                error = "Correo inválido";
                                return@Button }
                            if (contrasena.length < 6) {
                                error = "La contraseña debe tener al menos 6 caracteres";
                                return@Button }
                            if (contrasena != confirmar) {
                                error = "Las contraseñas no coinciden";
                                return@Button }

                            error = ""
                            vm.createUser(nombre, apellido, correo, contrasena)
                        },
                        enabled = !cargando,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(if (cargando) "Registrando..." else "Registrar")
                    }
                }
            }
        }
    }
}
