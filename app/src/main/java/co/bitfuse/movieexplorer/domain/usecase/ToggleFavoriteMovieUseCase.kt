package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class ToggleFavoriteMovieUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(movie: Movie) = movieRepository.toggleFavorite(movie)
}
