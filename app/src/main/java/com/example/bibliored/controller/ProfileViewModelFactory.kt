package com.example.bibliored.controller

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.bibliored.api.ApiAuthRepository
import com.example.bibliored.data.SessionPrefs
import com.example.bibliored.data.dataStore

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            val authRepository = ApiAuthRepository.default()
            val sessionPrefs = SessionPrefs(context.applicationContext.dataStore)
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(authRepository, sessionPrefs) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}