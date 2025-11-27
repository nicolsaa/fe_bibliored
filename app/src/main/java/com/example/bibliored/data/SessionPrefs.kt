package com.example.bibliored.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bibliored.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionPrefs(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID      = stringPreferencesKey("user_id") // Cambiado a stringPreferencesKey
        val USER_NAME    = stringPreferencesKey("user_name")
        val USER_EMAIL   = stringPreferencesKey("user_email")
    }

    val sessionFlow: Flow<Session> = context.dataStore.data.map { preferences ->
        Session(
            isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
            userId = preferences[Keys.USER_ID] ?: "", // Ahora incluimos el userId
            userName = preferences[Keys.USER_NAME] ?: "",
            userEmail = preferences[Keys.USER_EMAIL] ?: "",
        )
    }

    suspend fun setLoggedIn(id: String, name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = true
            preferences[Keys.USER_ID] = id // Guardamos el userId
            preferences[Keys.USER_NAME] = name
            preferences[Keys.USER_EMAIL] = email
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }

    // Función para obtener la sesión actual de forma síncrona (opcional)
    suspend fun getCurrentSession(): Session? {
        return context.dataStore.data.map { preferences ->
            Session(
                isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
                userId = preferences[Keys.USER_ID] ?: "",
                userName = preferences[Keys.USER_NAME] ?: "",
                userEmail = preferences[Keys.USER_EMAIL] ?: "",
            )
        }.map { if (it.isLoggedIn) it else null }.first()
    }
}