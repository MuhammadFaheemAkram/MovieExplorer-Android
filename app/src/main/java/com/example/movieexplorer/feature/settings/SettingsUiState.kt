package com.example.movieexplorer.feature.settings

import com.example.movieexplorer.domain.model.ContentLanguage

data class SettingsUiState(
    val darkModeEnabled: Boolean = false,
    val selectedLanguage: ContentLanguage = ContentLanguage.English,
    val availableLanguages: List<ContentLanguage> = ContentLanguage.entries,
)
