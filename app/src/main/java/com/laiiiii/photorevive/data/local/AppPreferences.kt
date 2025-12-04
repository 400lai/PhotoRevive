package com.laiiiii.photorevive.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_prefs")

class AppPreferences(private val context: Context) {
    private val dataStore = context.dataStore

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val CURRENT_USER_ID = longPreferencesKey("current_user_id")
    }

    suspend fun setLoggedIn(userId: Long) {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = true
            prefs[CURRENT_USER_ID] = userId
        }
    }

    suspend fun setLoggedOut() {
        dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN] = false
            prefs.remove(CURRENT_USER_ID)
        }
    }

    val isLoggedIn = dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    val currentUserId = dataStore.data.map { prefs ->
        prefs[CURRENT_USER_ID] ?: -1L
    }
}