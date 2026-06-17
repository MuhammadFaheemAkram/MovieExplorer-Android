package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.domain.model.ContentLanguage
import com.example.movieexplorer.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateContentLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(language: ContentLanguage) =
        settingsRepository.setContentLanguage(language)
}
