package com.example.movieexplorer.testing

import com.example.movieexplorer.domain.model.Movie
import com.example.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMovieRepository : MovieRepository {
    val trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())

    var refreshResult: Result<List<Movie>> = Result.success(emptyList())
    var searchResult: Result<List<Movie>> = Result.success(emptyList())
    var lastSearchQuery: String? = null

    override fun observeTrendingMovies(): Flow<List<Movie>> = trendingMovies

    override fun observeFavoriteMovies(): Flow<List<Movie>> = favoriteMovies

    override suspend fun refreshTrendingMovies() {
        trendingMovies.value = refreshResult.getOrThrow()
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        lastSearchQuery = query
        return searchResult.getOrThrow()
    }

    override suspend fun getMovieDetails(id: Int): Movie =
        trendingMovies.value.firstOrNull { movie -> movie.id == id }
            ?: favoriteMovies.value.first { movie -> movie.id == id }

    override suspend fun toggleFavorite(movie: Movie) {
        val isCurrentlyFavorite = favoriteMovies.value.any { favorite -> favorite.id == movie.id }
        favoriteMovies.value = if (isCurrentlyFavorite) {
            favoriteMovies.value.filterNot { favorite -> favorite.id == movie.id }
        } else {
            favoriteMovies.value + movie.copy(isFavorite = true)
        }
        trendingMovies.value = trendingMovies.value.map { trendingMovie ->
            if (trendingMovie.id == movie.id) {
                trendingMovie.copy(isFavorite = !isCurrentlyFavorite)
            } else {
                trendingMovie
            }
        }
    }
}
