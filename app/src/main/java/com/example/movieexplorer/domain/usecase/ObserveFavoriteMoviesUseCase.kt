package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class ObserveFavoriteMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    operator fun invoke() = movieRepository.observeFavoriteMovies()
}
