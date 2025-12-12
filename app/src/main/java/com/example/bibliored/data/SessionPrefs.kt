package com.example.bibliored.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bibliored.model.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

/**
 * Interface for Session preferences to decouple ViewModel from Android dependencies.
 */
interface ISessionPrefs {
    val sessionFlow: Flow<Session>
    suspend fun setLoggedIn(id: String, name: String, email: String)
    suspend fun clear()
    suspend fun getCurrentSession(): Session?
}

class SessionPrefs(private val dataStore: DataStore<Preferences>) : ISessionPrefs {

    private object Keys {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID      = stringPreferencesKey("user_id")
        val USER_NAME    = stringPreferencesKey("user_name")
        val USER_EMAIL   = stringPreferencesKey("user_email")
    }

    override val sessionFlow: Flow<Session> = dataStore.data.map { preferences ->
        Session(
            isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
            userId = preferences[Keys.USER_ID] ?: "",
            userName = preferences[Keys.USER_NAME] ?: "",
            userEmail = preferences[Keys.USER_EMAIL] ?: "",
        )
    }

    override suspend fun setLoggedIn(id: String, name: String, email: String) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_LOGGED_IN] = true
            preferences[Keys.USER_ID] = id
            preferences[Keys.USER_NAME] = name
            preferences[Keys.USER_EMAIL] = email
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    override suspend fun getCurrentSession(): Session? {
        return dataStore.data.map { preferences ->
            Session(
                isLoggedIn = preferences[Keys.IS_LOGGED_IN] ?: false,
                userId = preferences[Keys.USER_ID] ?: "",
                userName = preferences[Keys.USER_NAME] ?: "",
                userEmail = preferences[Keys.USER_EMAIL] ?: "",
            )
        }.map { if (it.isLoggedIn) it else null }.first()
    }
}
