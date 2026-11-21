package co.bitfuse.movieexplorer.testing

import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft
import co.bitfuse.movieexplorer.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeMovieRepository : MovieRepository {
    val trendingMovies = MutableStateFlow<List<Movie>>(emptyList())
    val favoriteMovies = MutableStateFlow<List<Movie>>(emptyList())
    val catalogVersion = MutableStateFlow(0)

    var refreshResult: Result<List<Movie>> = Result.success(emptyList())
    var searchResult: Result<List<Movie>> = Result.success(emptyList())
    var lastSearchQuery: String? = null
    var searchCallCount = 0
    var toggleFavoriteError: Throwable? = null
    var createMovieError: Throwable? = null
    var lastCreatedMovie: MovieDraft? = null
    var updateMovieError: Throwable? = null
    var lastUpdatedMovie: Pair<Int, MovieDraft>? = null
    var deleteMovieError: Throwable? = null
    val deletedMovieIds = mutableListOf<Int>()

    override fun observeTrendingMovies(): Flow<List<Movie>> = trendingMovies

    override fun observeFavoriteMovies(): Flow<List<Movie>> = favoriteMovies

    override fun observeCatalogVersion(): Flow<Int> = catalogVersion

    override suspend fun refreshTrendingMovies() {
        trendingMovies.value = refreshResult.getOrThrow()
    }

    override suspend fun searchMovies(query: String): List<Movie> {
        lastSearchQuery = query
        searchCallCount++
        return searchResult.getOrThrow()
    }

    override suspend fun getMovieDetails(id: Int): Movie =
        trendingMovies.value.firstOrNull { movie -> movie.id == id }
            ?: favoriteMovies.value.first { movie -> movie.id == id }

    override suspend fun createMovie(movieDraft: MovieDraft): Movie {
        createMovieError?.let { error -> throw error }
        lastCreatedMovie = movieDraft
        val created = Movie(
            id = (trendingMovies.value.maxOfOrNull { movie -> movie.id } ?: 0) + 1,
            title = movieDraft.title,
            overview = movieDraft.overview,
            posterUrl = movieDraft.posterUrl,
            backdropUrl = movieDraft.backdropUrl,
            rating = movieDraft.rating,
            releaseYear = movieDraft.releaseYear.toString(),
            genres = movieDraft.genres,
            isFavorite = false,
        )
        trendingMovies.value = trendingMovies.value + created
        return created
    }

    override suspend fun updateMovie(id: Int, movieDraft: MovieDraft): Movie {
        updateMovieError?.let { error -> throw error }
        lastUpdatedMovie = id to movieDraft
        val updated = getMovieDetails(id).copy(
            title = movieDraft.title,
            overview = movieDraft.overview,
            posterUrl = movieDraft.posterUrl,
            backdropUrl = movieDraft.backdropUrl,
            rating = movieDraft.rating,
            releaseYear = movieDraft.releaseYear.toString(),
            genres = movieDraft.genres,
        )
        trendingMovies.value = trendingMovies.value.map { movie -> if (movie.id == id) updated else movie }
        return updated
    }

    override suspend fun deleteMovie(id: Int) {
        deleteMovieError?.let { error -> throw error }
        deletedMovieIds += id
        catalogVersion.value++
        trendingMovies.value = trendingMovies.value.filterNot { movie -> movie.id == id }
        favoriteMovies.value = favoriteMovies.value.filterNot { movie -> movie.id == id }
    }

    override suspend fun toggleFavorite(movie: Movie) {
        toggleFavoriteError?.let { error -> throw error }
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
