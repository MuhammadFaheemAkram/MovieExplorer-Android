package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.repository.SettingsRepository
import javax.inject.Inject

class ObserveSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke() = settingsRepository.observeSettings()
}
