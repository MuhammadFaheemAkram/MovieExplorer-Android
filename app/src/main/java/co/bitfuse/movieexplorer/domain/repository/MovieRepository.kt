package co.bitfuse.movieexplorer.domain.repository

import co.bitfuse.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeTrendingMovies(): Flow<List<Movie>>
    fun observeFavoriteMovies(): Flow<List<Movie>>
    suspend fun refreshTrendingMovies()
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(id: Int): Movie
    suspend fun toggleFavorite(movie: Movie)
}
