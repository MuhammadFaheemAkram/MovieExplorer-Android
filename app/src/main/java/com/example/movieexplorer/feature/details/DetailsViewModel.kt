package com.example.movieexplorer.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import com.example.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
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
            toggleFavoriteMovieUseCase(currentState.movie.toDomain())
            _uiState.value = MovieDetailsUiState.Success(
                currentState.movie.copy(isFavorite = !currentState.movie.isFavorite),
            )
        }
    }
}
