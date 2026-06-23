package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailsUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(id: Int) = movieRepository.getMovieDetails(id)
}
