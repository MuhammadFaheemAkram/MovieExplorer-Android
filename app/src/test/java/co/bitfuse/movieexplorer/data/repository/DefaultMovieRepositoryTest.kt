package co.bitfuse.movieexplorer.data.repository

import co.bitfuse.movieexplorer.data.model.MovieRequestDto
import co.bitfuse.movieexplorer.data.model.MovieDto
import co.bitfuse.movieexplorer.data.model.MovieListResponseDto
import co.bitfuse.movieexplorer.data.remote.MovieApiService
import co.bitfuse.movieexplorer.data.remote.MovieExplorerApiException
import co.bitfuse.movieexplorer.domain.model.Movie
import co.bitfuse.movieexplorer.domain.model.MovieDraft
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class DefaultMovieRepositoryTest {

    private val api = FakeRetrofitMovieApiService()
    private val repository = DefaultMovieRepository(api)

    @Test
    fun `refresh loads trending movies with favorite flags from the backend favorites`() = runTest {
        api.movies = listOf(movieDto(id = 1), movieDto(id = 2))
        api.favorites = listOf(movieDto(id = 2, isFavorite = true))

        repository.refreshTrendingMovies()
        val trending = repository.observeTrendingMovies().first()

        assertEquals(listOf(1, 2), trending.map { movie -> movie.id })
        assertFalse(trending[0].isFavorite)
        assertTrue(trending[1].isFavorite)
        assertEquals("2026", trending[0].releaseYear)
    }

    @Test
    fun `toggle adds then removes favorite and updates trending flags`() = runTest {
        api.movies = listOf(movieDto(id = 1))
        repository.refreshTrendingMovies()

        repository.toggleFavorite(repository.observeTrendingMovies().first().single())
        val afterAdd = repository.observeTrendingMovies().first().single()
        assertTrue(afterAdd.isFavorite)
        assertEquals(listOf(1), api.addedFavoriteIds)

        repository.toggleFavorite(afterAdd)
        assertFalse(repository.observeTrendingMovies().first().single().isFavorite)
        assertEquals(listOf(1), api.removedFavoriteIds)
    }

    @Test
    fun `blank search returns empty list without calling the backend`() = runTest {
        val results = repository.searchMovies("   ")

        assertTrue(results.isEmpty())
        assertNull(api.lastSearch)
    }

    @Test
    fun `search sends trimmed query`() = runTest {
        api.movies = listOf(movieDto(id = 3))

        val results = repository.searchMovies("  star ")

        assertEquals("star", api.lastSearch)
        assertEquals(listOf(3), results.map(Movie::id))
    }

    @Test
    fun `connection failure becomes a readable api exception`() = runTest {
        api.error = IOException("Failed to connect to /10.0.2.2:8000")

        val exception = captureApiException { repository.getMovieDetails(1) }

        assertEquals(
            "Could not reach the Movie Explorer server. Make sure the backend is running.",
            exception.message,
        )
    }

    @Test
    fun `http error uses the backend detail message`() = runTest {
        val body = """{"detail":"Movie with id 99 was not found."}"""
            .toResponseBody("application/json".toMediaType())
        api.error = HttpException(Response.error<MovieDto>(404, body))

        val exception = captureApiException { repository.getMovieDetails(99) }

        assertEquals("Movie with id 99 was not found.", exception.message)
        assertEquals(404, exception.httpCode)
    }

    @Test
    fun `create movie posts request and inserts it into trending by rating`() = runTest {
        api.movies = listOf(movieDto(id = 1, rating = 9.0), movieDto(id = 2, rating = 6.0))
        repository.refreshTrendingMovies()

        val created = repository.createMovie(
            MovieDraft(
                title = "Lantern Sea",
                overview = "A new movie.",
                posterUrl = "https://example.com/p.jpg",
                backdropUrl = "https://example.com/b.jpg",
                rating = 7.5,
                releaseYear = 2026,
                genres = listOf("Adventure"),
            ),
        )

        assertEquals("Lantern Sea", api.lastCreateRequest?.title)
        assertEquals(2026, api.lastCreateRequest?.releaseYear)
        assertEquals(3, created.id)
        assertEquals(listOf(1, 3, 2), repository.observeTrendingMovies().first().map(Movie::id))
    }

    @Test
    fun `update replaces the movie in trending and favorites and re-sorts`() = runTest {
        api.movies = listOf(movieDto(id = 1, rating = 9.0), movieDto(id = 2, rating = 6.0))
        api.favorites = listOf(movieDto(id = 2, rating = 6.0, isFavorite = true))
        repository.refreshTrendingMovies()

        repository.updateMovie(2, movieDraft(title = "Renamed", rating = 9.5))

        assertEquals(2, api.lastUpdate?.first)
        assertEquals(listOf(2, 1), repository.observeTrendingMovies().first().map(Movie::id))
        assertEquals("Renamed", repository.observeTrendingMovies().first().first().title)
        assertTrue(repository.observeTrendingMovies().first().first().isFavorite)
    }

    @Test
    fun `delete removes the movie from trending and favorites`() = runTest {
        api.movies = listOf(movieDto(id = 1), movieDto(id = 2))
        api.favorites = listOf(movieDto(id = 2, isFavorite = true))
        repository.refreshTrendingMovies()

        repository.deleteMovie(2)

        assertEquals(listOf(2), api.deletedIds)
        assertEquals(listOf(1), repository.observeTrendingMovies().first().map(Movie::id))
        api.favorites = emptyList() // the backend cascade-deleted the favorite row
        assertTrue(repository.observeFavoriteMovies().first().isEmpty())
    }

    @Test
    fun `validation error uses the first field message`() = runTest {
        val body = """{"detail":[{"loc":["body","rating"],"msg":"Input should be less than or equal to 10","type":"less_than_equal"}]}"""
            .toResponseBody("application/json".toMediaType())
        api.error = HttpException(Response.error<MovieDto>(422, body))

        val exception = captureApiException { repository.getMovieDetails(1) }

        assertEquals("rating: Input should be less than or equal to 10", exception.message)
    }

    private suspend fun captureApiException(block: suspend () -> Unit): MovieExplorerApiException {
        try {
            block()
        } catch (exception: MovieExplorerApiException) {
            return exception
        }
        fail("Expected MovieExplorerApiException")
        error("unreachable")
    }
}

private fun movieDto(id: Int, isFavorite: Boolean = false, rating: Double = 7.5) = MovieDto(
    id = id,
    title = "Movie $id",
    overview = "Overview $id",
    posterUrl = "https://example.com/poster-$id.jpg",
    backdropUrl = "https://example.com/backdrop-$id.jpg",
    rating = rating,
    releaseYear = 2026,
    genres = listOf("Drama"),
    isFavorite = isFavorite,
)

private fun movieDraft(title: String = "Lantern Sea", rating: Double = 7.5) = MovieDraft(
    title = title,
    overview = "A movie.",
    posterUrl = "https://example.com/p.jpg",
    backdropUrl = "https://example.com/b.jpg",
    rating = rating,
    releaseYear = 2026,
    genres = listOf("Adventure"),
)

/** In-memory stand-in for the Retrofit service; behaves like the FastAPI backend. */
private class FakeRetrofitMovieApiService : MovieApiService {
    var movies: List<MovieDto> = emptyList()
    var favorites: List<MovieDto> = emptyList()
    var error: Throwable? = null
    var lastSearch: String? = null
    var lastCreateRequest: MovieRequestDto? = null
    var lastUpdate: Pair<Int, MovieRequestDto>? = null
    val deletedIds = mutableListOf<Int>()
    val addedFavoriteIds = mutableListOf<Int>()
    val removedFavoriteIds = mutableListOf<Int>()

    override suspend fun getMovies(search: String?, limit: Int, offset: Int): MovieListResponseDto {
        error?.let { throw it }
        lastSearch = search
        return page(movies)
    }

    override suspend fun getMovieDetails(id: Int): MovieDto {
        error?.let { throw it }
        return movies.first { movie -> movie.id == id }
    }

    override suspend fun createMovie(request: MovieRequestDto): MovieDto {
        error?.let { throw it }
        lastCreateRequest = request
        val created = MovieDto(
            id = (movies.maxOfOrNull { movie -> movie.id } ?: 0) + 1,
            title = request.title,
            overview = request.overview,
            posterUrl = request.posterUrl,
            backdropUrl = request.backdropUrl,
            rating = request.rating,
            releaseYear = request.releaseYear,
            genres = request.genres,
        )
        movies = movies + created
        return created
    }

    override suspend fun updateMovie(id: Int, request: MovieRequestDto): MovieDto {
        error?.let { throw it }
        lastUpdate = id to request
        val existing = movies.first { movie -> movie.id == id }
        val updated = existing.copy(
            title = request.title,
            overview = request.overview,
            posterUrl = request.posterUrl,
            backdropUrl = request.backdropUrl,
            rating = request.rating,
            releaseYear = request.releaseYear,
            genres = request.genres,
            isFavorite = favorites.any { favorite -> favorite.id == id },
        )
        movies = movies.map { movie -> if (movie.id == id) updated else movie }
        return updated
    }

    override suspend fun deleteMovie(id: Int) {
        error?.let { throw it }
        deletedIds += id
        movies = movies.filterNot { movie -> movie.id == id }
    }

    override suspend fun getFavoriteMovies(limit: Int, offset: Int): MovieListResponseDto {
        error?.let { throw it }
        return page(favorites)
    }

    override suspend fun addFavorite(movieId: Int): MovieDto {
        error?.let { throw it }
        addedFavoriteIds += movieId
        return movies.first { movie -> movie.id == movieId }.copy(isFavorite = true)
    }

    override suspend fun removeFavorite(movieId: Int) {
        error?.let { throw it }
        removedFavoriteIds += movieId
    }

    private fun page(items: List<MovieDto>) =
        MovieListResponseDto(items = items, total = items.size, limit = 50, offset = 0)
}
