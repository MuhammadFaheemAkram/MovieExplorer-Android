package co.bitfuse.movieexplorer.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.movieexplorer.domain.model.ContentLanguage
import co.bitfuse.movieexplorer.domain.usecase.ObserveSettingsUseCase
import co.bitfuse.movieexplorer.domain.usecase.UpdateContentLanguageUseCase
import co.bitfuse.movieexplorer.domain.usecase.UpdateThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettingsUseCase: ObserveSettingsUseCase,
    private val updateThemeUseCase: UpdateThemeUseCase,
    private val updateContentLanguageUseCase: UpdateContentLanguageUseCase,
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = observeSettingsUseCase()
        .map { settings ->
            SettingsUiState(
                darkModeEnabled = settings.darkModeEnabled,
                selectedLanguage = settings.contentLanguage,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = SettingsUiState(),
        )

    fun setDarkModeEnabled(enabled: Boolean) {
        viewModelScope.launch {
            updateThemeUseCase(enabled)
        }
    }

    fun setContentLanguage(language: ContentLanguage) {
        viewModelScope.launch {
            updateContentLanguageUseCase(language)
        }
    }
}
