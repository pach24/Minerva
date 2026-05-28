package com.minerva.app.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.minerva.app.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SessionDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val EMAIL = stringPreferencesKey("email")
    }

    val sessionFlow: Flow<AuthSession?> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            val token = prefs[ACCESS_TOKEN]
            val email = prefs[EMAIL]
            if (token != null && email != null) AuthSession(token, email) else null
        }

    suspend fun getToken(): String? = dataStore.data
        .catch { emit(emptyPreferences()) }
        .first()[ACCESS_TOKEN]

    suspend fun saveSession(token: String, email: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = token
            prefs[EMAIL] = email
        }
    }

    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}
