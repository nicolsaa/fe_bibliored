package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.AuthRepository
import com.example.bibliored.data.SessionPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserProfile(
    val fullName: String,
    val email: String,
    val photoUrl: String
)

class ProfileViewModel(private val authRepository: AuthRepository, private val sessionPrefs: SessionPrefs) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun loadUserProfile() {
        viewModelScope.launch {
            _userProfile.value = UserProfile(
                fullName = "Nombre de Usuario",
                email = "usuario@example.com",
                photoUrl = ""
            )
        }
    }

    fun updateProfilePicture() {
        viewModelScope.launch {
            // En una app real, aquí se abriría la galería para seleccionar una foto.
            // Como simulación, cambiaremos la URL a una imagen de ejemplo.
            _userProfile.value = _userProfile.value?.copy(photoUrl = "new_photo")
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            sessionPrefs.clear()
        }
    }
}
