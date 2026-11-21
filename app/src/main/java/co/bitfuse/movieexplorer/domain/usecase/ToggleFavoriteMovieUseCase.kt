package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

// Favorites are stored on the backend, so toggling can fail (no connection, server error).
// Returning a result instead of throwing keeps a failed tap from crashing the app.
class ToggleFavoriteMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movie: Movie): MovieExplorerResult<Unit> = runCatching {
        movieRepository.toggleFavorite(movie)
    }.fold(
        onSuccess = { MovieExplorerResult.Success(Unit) },
        onFailure = { throwable ->
            MovieExplorerResult.Error(
                message = throwable.message ?: "Could not update favorites.",
                cause = throwable,
            )
        },
    )
}
