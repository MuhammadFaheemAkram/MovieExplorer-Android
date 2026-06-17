package com.example.movieexplorer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.movieexplorer.domain.model.ContentLanguage
import com.example.movieexplorer.domain.model.UserSettings
import com.example.movieexplorer.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override fun observeSettings(): Flow<UserSettings> =
        dataStore.data.map { preferences ->
            UserSettings(
                darkModeEnabled = preferences[PreferencesKeys.DarkModeEnabled] ?: false,
                contentLanguage = ContentLanguage.fromCode(
                    preferences[PreferencesKeys.ContentLanguage] ?: ContentLanguage.English.code,
                ),
            )
        }

    override suspend fun setDarkModeEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.DarkModeEnabled] = enabled
        }
    }

    override suspend fun setContentLanguage(language: ContentLanguage) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.ContentLanguage] = language.code
        }
    }

    private object PreferencesKeys {
        val DarkModeEnabled = booleanPreferencesKey("dark_mode_enabled")
        val ContentLanguage = stringPreferencesKey("content_language")
    }
}
