package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
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
