package co.bitfuse.movieexplorer.domain.repository

import co.bitfuse.movieexplorer.domain.model.ContentLanguage
import co.bitfuse.movieexplorer.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<UserSettings>
    suspend fun setDarkModeEnabled(enabled: Boolean)
    suspend fun setContentLanguage(language: ContentLanguage)
}
