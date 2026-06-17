package com.example.movieexplorer.domain.repository

import com.example.movieexplorer.domain.model.ContentLanguage
import com.example.movieexplorer.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun setDarkModeEnabled(enabled: Boolean)
    suspend fun setContentLanguage(language: ContentLanguage)
}
