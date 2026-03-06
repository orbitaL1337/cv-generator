package com.fitplannerpro.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("fitplanner_settings")

class UserSettingsStore(private val context: Context) {
    private val darkModeKey = booleanPreferencesKey("dark_mode")

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { it[darkModeKey] ?: false }

    suspend fun setDarkMode(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[darkModeKey] = value
        }
    }
}
