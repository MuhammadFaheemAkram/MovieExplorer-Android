package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository,
) {
    suspend operator fun invoke(query: String) = movieRepository.searchMovies(query)
}
