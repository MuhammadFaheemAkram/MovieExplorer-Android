package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class UpdateMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(id: Int, movieDraft: MovieDraft): MovieExplorerResult<Movie> = runCatching {
        movieRepository.updateMovie(id, movieDraft)
    }.fold(
        onSuccess = { movie -> MovieExplorerResult.Success(movie) },
        onFailure = { throwable ->
            MovieExplorerResult.Error(
                message = throwable.message ?: "Could not save your changes.",
                cause = throwable,
            )
        },
    )
}
