package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class ObserveFavoriteMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke() = movieRepository.observeFavoriteMovies()
}
