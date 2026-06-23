package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.model.ContentLanguage
import co.bitfuse.movieexplorer.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateContentLanguageUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(language: ContentLanguage) =
        settingsRepository.setContentLanguage(language)
}
