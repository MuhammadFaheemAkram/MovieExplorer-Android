package co.bitfuse.movieexplorer.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.movieexplorer.domain.usecase.ObserveFavoriteMoviesUseCase
import co.bitfuse.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    observeFavoriteMoviesUseCase: ObserveFavoriteMoviesUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
) : ViewModel() {

    val uiState: StateFlow<FavoritesUiState> = observeFavoriteMoviesUseCase()
        .map { movies ->
            FavoritesUiState(movies = movies.map { movie -> movie.toFavoriteUiModel() })
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = FavoritesUiState(),
        )

    fun removeFavorite(movie: FavoriteMovieUiModel) {
        viewModelScope.launch {
            toggleFavoriteMovieUseCase(movie.toDomain())
        }
    }
}
