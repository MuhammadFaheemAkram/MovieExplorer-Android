package co.bitfuse.movieexplorer.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.domain.usecase.DeleteMovieUseCase
import co.bitfuse.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import co.bitfuse.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getMovieDetailsUseCase: GetMovieDetailsUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<MovieDetailsUiState>(MovieDetailsUiState.Loading)
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _uiState.value = MovieDetailsUiState.Loading
            runCatching {
                getMovieDetailsUseCase(movieId).toDetailsUiModel()
            }.onSuccess { movie ->
                _uiState.value = MovieDetailsUiState.Success(movie)
            }.onFailure { throwable ->
                _uiState.value = MovieDetailsUiState.Error(
                    message = throwable.message ?: "Could not load movie details.",
                )
            }
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value as? MovieDetailsUiState.Success ?: return
        viewModelScope.launch {
            val result = toggleFavoriteMovieUseCase(currentState.movie.toDomain())
            // Only flip the heart once the backend confirmed the change.
            if (result is MovieExplorerResult.Success) {
                _uiState.value = currentState.copy(
                    movie = currentState.movie.copy(isFavorite = !currentState.movie.isFavorite),
                )
            }
        }
    }

    // Called after the user confirmed the delete dialog.
    fun deleteMovie() {
        val currentState = _uiState.value as? MovieDetailsUiState.Success ?: return
        if (currentState.isDeleting) return
        viewModelScope.launch {
            _uiState.value = currentState.copy(isDeleting = true, actionErrorMessage = null)
            when (val result = deleteMovieUseCase(currentState.movie.id)) {
                is MovieExplorerResult.Success -> {
                    _uiState.value = MovieDetailsUiState.Deleted
                }

                is MovieExplorerResult.Error -> {
                    _uiState.value = currentState.copy(
                        isDeleting = false,
                        actionErrorMessage = result.message,
                    )
                }
            }
        }
    }
}
