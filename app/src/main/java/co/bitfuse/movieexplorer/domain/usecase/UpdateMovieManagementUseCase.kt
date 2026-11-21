package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.repository.SettingsRepository
import javax.inject.Inject

class UpdateMovieManagementUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(enabled: Boolean) =
        settingsRepository.setMovieManagementEnabled(enabled)
}
