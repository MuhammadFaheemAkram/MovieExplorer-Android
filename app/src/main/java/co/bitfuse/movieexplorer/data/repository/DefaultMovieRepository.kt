package co.bitfuse.movieexplorer.data.repository

import co.bitfuse.movieexplorer.data.mapper.toDomain
import co.bitfuse.movieexplorer.data.mapper.toEntity
import co.bitfuse.movieexplorer.data.local.MovieDao
import co.bitfuse.movieexplorer.data.remote.MovieApiService
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMovieRepository @Inject constructor(
    private val apiService: MovieApiService,
    private val movieDao: MovieDao,
) : MovieRepository {

    private val cachedTrendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val favoriteMovieIds = movieDao.observeFavoriteMovieIds()
        .map { ids -> ids.toSet() }

    // The repository hides whether data comes from network, memory, or Room.
    override fun observeTrendingMovies(): Flow<List<Movie>> =
        combine(cachedTrendingMovies, favoriteMovieIds) { movies, favoriteIds ->
            movies.withFavoriteState(favoriteIds)
        }

    override fun observeFavoriteMovies(): Flow<List<Movie>> =
        movieDao.observeFavoriteMovies().map { favoriteMovies ->
            favoriteMovies.map { entity -> entity.toDomain() }
        }

    override suspend fun refreshTrendingMovies() {
        val favoriteIds = favoriteMovieIds.first()
        val remoteMovies = apiService.getTrendingMovies().map { dto ->
            dto.toDomain(isFavorite = dto.id in favoriteIds)
        }
        cachedTrendingMovies.value = remoteMovies
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        val favoriteIds = favoriteMovieIds.first()
        return apiService.searchMovies(query).map { dto ->
            dto.toDomain(isFavorite = dto.id in favoriteIds)
        }
    }

    override suspend fun getMovieDetails(id: Int): Movie {
        val favoriteMovie = movieDao.getFavoriteMovie(id)
        val cachedMovie = cachedTrendingMovies.value.firstOrNull { it.id == id }
        if (cachedMovie != null) return cachedMovie.copy(isFavorite = favoriteMovie != null)
        if (favoriteMovie != null) return favoriteMovie.toDomain()

        return apiService.getMovieDetails(id)
            .toDomain(isFavorite = movieDao.isFavorite(id))
    }

    override suspend fun toggleFavorite(movie: Movie) {
        if (movieDao.isFavorite(movie.id)) {
            movieDao.deleteFavoriteMovieById(movie.id)
        } else {
            movieDao.upsertFavoriteMovie(movie.toEntity())
        }
    }

    private fun List<Movie>.withFavoriteState(favoriteIds: Set<Int>): List<Movie> =
        map { movie -> movie.copy(isFavorite = movie.id in favoriteIds) }
}
