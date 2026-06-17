package com.example.movieexplorer.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieexplorer.core.common.MovieExplorerResult
import com.example.movieexplorer.domain.usecase.GetTrendingMoviesUseCase
import com.example.movieexplorer.domain.usecase.RefreshTrendingMoviesUseCase
import com.example.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val refreshTrendingMoviesUseCase: RefreshTrendingMoviesUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(HomeViewModelState(isLoading = true))

    // StateFlow gives Compose one observable, lifecycle-aware source of truth for this screen.
    val uiState: StateFlow<HomeUiState> = combine(
        viewModelState,
        getTrendingMoviesUseCase(),
    ) { state, movies ->
        HomeUiState(
            isLoading = state.isLoading,
            isRefreshing = state.isRefreshing,
            movies = movies.map { it.toUiModel() },
            errorMessage = state.errorMessage,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = HomeUiState(isLoading = true),
    )

    init {
        loadTrendingMovies(isInitialLoad = true)
    }

    fun retry() {
        loadTrendingMovies(isInitialLoad = uiState.value.movies.isEmpty())
    }

    fun refresh() {
        loadTrendingMovies(isInitialLoad = false)
    }

    fun toggleFavorite(movie: MovieUiModel) {
        viewModelScope.launch {
            toggleFavoriteMovieUseCase(movie.toDomain())
        }
    }

    private fun loadTrendingMovies(isInitialLoad: Boolean) {
        viewModelScope.launch {
            viewModelState.update { state ->
                state.copy(
                    isLoading = isInitialLoad,
                    isRefreshing = !isInitialLoad,
                    errorMessage = null,
                )
            }

            when (val result = refreshTrendingMoviesUseCase()) {
                is MovieExplorerResult.Success -> {
                    viewModelState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null,
                        )
                    }
                }

                is MovieExplorerResult.Error -> {
                    viewModelState.update { state ->
                        state.copy(
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = result.message,
                        )
                    }
                }
            }
        }
    }
}

private data class HomeViewModelState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
)
