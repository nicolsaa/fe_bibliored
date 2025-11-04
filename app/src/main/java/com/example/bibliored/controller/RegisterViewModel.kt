package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.ApiAuthRepository
import com.example.bibliored.api.AuthRepository
import com.example.bibliored.model.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface RegisterState {
    object Idle : RegisterState
    object Loading : RegisterState
    data class Success(val usuario: Usuario) : RegisterState
    data class Error(val mensaje: String) : RegisterState
}

class RegisterViewModel (
    private val repo: AuthRepository = ApiAuthRepository.default()
    ) : ViewModel() {
        private val _estado: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState.Idle)

        val estado: StateFlow<RegisterState> = _estado.asStateFlow()

        fun createUser(nombre: String, apellido: String, correo: String, contrasena: String) {
            viewModelScope.launch {
                _estado.value = RegisterState.Loading
                val res = repo.createUser(nombre, apellido, correo, contrasena)

                if (res.isSuccess) {
                    _estado.value = res.fold(
                        onSuccess = { RegisterState.Success(it) },
                        onFailure = { RegisterState.Error(it.message ?: "Error al crear usuario") }
                    )
                } else {
                    _estado.value = RegisterState.Error(res.exceptionOrNull()?.message ?: "Error al crear usuario")
                }
            }
        }
    }