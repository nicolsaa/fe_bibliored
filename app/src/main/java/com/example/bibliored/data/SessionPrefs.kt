package com.example.bibliored.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bibliored.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session_prefs")

class SessionPrefs(private val context: Context) {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID      = longPreferencesKey("user_id")
        val USER_NAME    = stringPreferencesKey("user_name")
        val USER_EMAIL   = stringPreferencesKey("user_email")
    }

    val sessionFlow: Flow<Session> = context.dataStore.data.map { preferences ->
        Session(
            isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
            //userId = preferences[Keys.USER_ID] ?: "",
            userName = preferences[Keys.USER_NAME] ?: "",
            userEmail = preferences[Keys.USER_EMAIL] ?: ""
        )
    }

    suspend fun setLoggedIn(id: Long, name: String, email: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = true
            //preferences[Keys.USER_ID] = id.toString()
            preferences[Keys.USER_NAME] = name
            preferences[Keys.USER_EMAIL] = email
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}