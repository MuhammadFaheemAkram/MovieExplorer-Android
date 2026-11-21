package co.bitfuse.movieexplorer.data.repository

import co.bitfuse.movieexplorer.data.mapper.toDomain
import co.bitfuse.movieexplorer.data.mapper.toRequestDto
import co.bitfuse.movieexplorer.data.remote.MovieApiService
import co.bitfuse.movieexplorer.data.remote.MovieExplorerApiException
import co.bitfuse.movieexplorer.data.remote.safeApiCall
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The backend is the single source of truth for movies AND favorites.
 *
 * Two in-memory StateFlows keep the screens reactive: when a favorite is toggled,
 * [favoriteMovies] changes and Home's heart icons update without another request.
 */
@Singleton
class DefaultMovieRepository @Inject constructor(
    private val apiService: MovieApiService,
) : MovieRepository {

    private val trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    private val catalogVersion = MutableStateFlow(0)

    override fun observeTrendingMovies(): Flow<List<Movie>> =
        combine(trendingMovies, favoriteMovies) { movies, favorites ->
            val favoriteIds = favorites.map { favorite -> favorite.id }.toSet()
            movies.map { movie -> movie.copy(isFavorite = movie.id in favoriteIds) }
        }

    // Every time the Favorites screen starts observing, fetch the latest list.
    // If that request fails, the last known list stays visible.
    override fun observeFavoriteMovies(): Flow<List<Movie>> =
        favoriteMovies.onStart {
            try {
                refreshFavoriteMovies()
            } catch (_: MovieExplorerApiException) {
                // Keep showing the cached favorites.
            }
        }

    override fun observeCatalogVersion(): Flow<Int> = catalogVersion

    override suspend fun refreshTrendingMovies() {
        val response = safeApiCall {
            apiService.getMovies(limit = TRENDING_PAGE_SIZE)
        }
        // Load favorites too, so Home's heart icons are correct from the start.
        refreshFavoriteMovies()
        trendingMovies.value = response.items.map { dto -> dto.toDomain() }
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return emptyList()

        val response = safeApiCall {
            apiService.getMovies(search = normalizedQuery, limit = SEARCH_PAGE_SIZE)
        }
        return response.items.map { dto -> dto.toDomain() }
    }

    override suspend fun getMovieDetails(id: Int): Movie =
        safeApiCall { apiService.getMovieDetails(id) }.toDomain()

    // The movie's current isFavorite (what the user sees) decides the action.
    // PUT and DELETE are idempotent on the backend, so a retried request is harmless.
    override suspend fun toggleFavorite(movie: Movie) {
        if (movie.isFavorite) {
            safeApiCall { apiService.removeFavorite(movie.id) }
            favoriteMovies.update { favorites ->
                favorites.filterNot { favorite -> favorite.id == movie.id }
            }
        } else {
            val addedMovie = safeApiCall { apiService.addFavorite(movie.id) }.toDomain()
            // Newest favorite first, matching the backend's ordering.
            favoriteMovies.update { favorites ->
                listOf(addedMovie) + favorites.filterNot { favorite -> favorite.id == movie.id }
            }
        }
    }

    override suspend fun createMovie(movieDraft: MovieDraft): Movie {
        val createdMovie = safeApiCall { apiService.createMovie(movieDraft.toRequestDto()) }.toDomain()
        // Show it on Home right away, without reloading the whole list.
        trendingMovies.update { movies -> (movies + createdMovie).sortedLikeBackend() }
        catalogVersion.update { version -> version + 1 }
        return createdMovie
    }

    override suspend fun updateMovie(id: Int, movieDraft: MovieDraft): Movie {
        val updatedMovie = safeApiCall { apiService.updateMovie(id, movieDraft.toRequestDto()) }.toDomain()
        // Replace the old copy in both caches; a new rating can move it in the Home order.
        trendingMovies.update { movies ->
            movies.map { movie -> if (movie.id == id) updatedMovie else movie }.sortedLikeBackend()
        }
        favoriteMovies.update { favorites ->
            favorites.map { favorite -> if (favorite.id == id) updatedMovie else favorite }
        }
        catalogVersion.update { version -> version + 1 }
        return updatedMovie
    }

    override suspend fun deleteMovie(id: Int) {
        safeApiCall { apiService.deleteMovie(id) }
        // The backend also deleted its favorite row (ON DELETE CASCADE); mirror that here.
        trendingMovies.update { movies -> movies.filterNot { movie -> movie.id == id } }
        favoriteMovies.update { favorites -> favorites.filterNot { favorite -> favorite.id == id } }
        catalogVersion.update { version -> version + 1 }
    }

    // Same order as `GET /movies`: best rated first, then oldest id.
    private fun List<Movie>.sortedLikeBackend(): List<Movie> =
        sortedWith(compareByDescending<Movie> { movie -> movie.rating }.thenBy { movie -> movie.id })

    private suspend fun refreshFavoriteMovies() {
        val response = safeApiCall {
            apiService.getFavoriteMovies(limit = FAVORITES_PAGE_SIZE)
        }
        favoriteMovies.value = response.items.map { dto -> dto.toDomain() }
    }

    private companion object {
        // The app has no paging yet (see docs/ROADMAP.md, Priority 5), so it loads
        // one generous page. The backend allows up to 100 items per request.
        const val TRENDING_PAGE_SIZE = 50
        const val SEARCH_PAGE_SIZE = 50
        const val FAVORITES_PAGE_SIZE = 100
    }
}
