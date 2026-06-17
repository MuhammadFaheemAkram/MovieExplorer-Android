package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(darkModeEnabled: Boolean) =
        settingsRepository.setDarkModeEnabled(darkModeEnabled)
}
