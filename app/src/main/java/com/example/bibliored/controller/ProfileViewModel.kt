package com.example.bibliored.controller

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bibliored.api.AuthRepository
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.model.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Address(
    val street: String = "",
    val number: String = "",
    val commune: String = "",
    val region: String = ""
) {
    fun toDisplayString(): String {
        if (street.isBlank() && number.isBlank() && commune.isBlank() && region.isBlank()) {
            return ""
        }
        return "$street $number, $commune, $region"
    }
}

data class UserProfile(
    val fullName: String,
    val email: String,
    val photoUrl: String,
    val address: Address?
)

class ProfileViewModel(private val authRepository: AuthRepository, private val sessionPrefs: SessionPrefs) : ViewModel() {

    private val _userProfile = MutableStateFlow<UserProfile?>(null)
    val userProfile: StateFlow<UserProfile?> = _userProfile

    fun loadUserProfile() {
        if (_userProfile.value == null) {
            viewModelScope.launch {
                val session = sessionPrefs.sessionFlow.first()
                if (session.isLoggedIn) {
                    _userProfile.value = UserProfile(
                        fullName = session.userName,
                        email = session.userEmail,
                        photoUrl = "",
                        address = null
                    )
                }
            }
        }
    }

    fun updateAddress(street: String, number: String, commune: String, region: String) {
        viewModelScope.launch {
            val newAddress = Address(street, number, commune, region)
            _userProfile.value = _userProfile.value?.copy(address = newAddress)
        }
    }

    fun updateProfilePicture() {
        viewModelScope.launch {
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