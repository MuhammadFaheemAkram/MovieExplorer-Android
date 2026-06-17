package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.core.common.MovieExplorerResult
import com.example.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class RefreshTrendingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(): MovieExplorerResult<Unit> = runCatching {
        movieRepository.refreshTrendingMovies()
    }.fold(
        onSuccess = { MovieExplorerResult.Success(Unit) },
        onFailure = { throwable ->
            MovieExplorerResult.Error(
                message = throwable.message ?: "Could not refresh trending movies.",
                cause = throwable,
            )
        },
    )
}
