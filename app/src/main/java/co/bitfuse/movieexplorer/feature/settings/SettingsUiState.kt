package co.bitfuse.movieexplorer.feature.settings

import co.bitfuse.movieexplorer.domain.model.ContentLanguage

data class SettingsUiState(
    val darkModeEnabled: Boolean = false,
    val selectedLanguage: ContentLanguage = ContentLanguage.English,
    val movieManagementEnabled: Boolean = false,
    val availableLanguages: List<ContentLanguage> = ContentLanguage.entries,
)
