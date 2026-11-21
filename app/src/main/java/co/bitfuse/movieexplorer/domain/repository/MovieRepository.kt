package co.bitfuse.movieexplorer.domain.repository

import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeTrendingMovies(): Flow<List<Movie>>
    fun observeFavoriteMovies(): Flow<List<Movie>>

    // Emits a new number after every create / update / delete, so screens that keep
    // their own copy of movies (like Search results) know to reload.
    fun observeCatalogVersion(): Flow<Int>
    suspend fun refreshTrendingMovies()
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(id: Int): Movie
    suspend fun toggleFavorite(movie: Movie)
    suspend fun createMovie(movieDraft: MovieDraft): Movie
    suspend fun updateMovie(id: Int, movieDraft: MovieDraft): Movie
    suspend fun deleteMovie(id: Int)
}
