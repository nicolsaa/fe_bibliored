package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.AuthRepository
import com.example.bibliored.api.ApiAuthRepository
import com.example.bibliored.model.Usuario
import com.example.bibliored.view.FormScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginState {
    object Idle : LoginState
    object Loading : LoginState
    data class Success(val usuario: Usuario) : LoginState
    data class Error(val mensaje: String) : LoginState
}

class AuthViewModel(
private val repo: AuthRepository = ApiAuthRepository.default()
) : ViewModel() {

    private val _estado: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.Idle)
    val estado: StateFlow<LoginState> = _estado.asStateFlow()

    // Nuevo: estado de existencia de usuario
    private val _userExists: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val userExists: StateFlow<Boolean?> = _userExists.asStateFlow()

    fun login(correo: String, contrasena: String) {
        // Validación mínima local
        val emailOk = correo.contains("@") && correo.contains(".")
        val passOk = contrasena.length >= 6
        if (!emailOk) {
            _estado.value = LoginState.Error("Correo inválido")
            return
        }
        if (!passOk) {
            _estado.value = LoginState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _estado.value = LoginState.Loading
            val res = repo.login(correo, contrasena)
            _estado.value = res.fold(
                onSuccess = { LoginState.Success(it) },
                onFailure = { LoginState.Error("Usuario no válido") }
            )
        }
    }

    fun reset() {
        _estado.value = LoginState.Idle
    }

    // Nuevo: verificar existencia de usuario
    fun checkUserExists(correo: String) {
        viewModelScope.launch {
            val exists = repo.userExists(correo)
            _userExists.value = exists
        }
    }

    // Nuevo: crear usuario y luego iniciar sesión
    fun createUserAndLogin(nombre: String, apellido: String, correo: String, contrasena: String) {
        viewModelScope.launch {
            _estado.value = LoginState.Loading
            val res = repo.createUser(nombre, apellido, correo, contrasena)
            if (res.isSuccess) {
                val loginRes = repo.login(correo, contrasena)
                _estado.value = loginRes.fold(
                    onSuccess = { LoginState.Success(it) },
                    onFailure = { LoginState.Error(it.message ?: "Error de autenticación") }
                )
            } else {
                _estado.value = LoginState.Error(res.exceptionOrNull()?.message ?: "Error creando usuario")
            }
        }
    }
}
