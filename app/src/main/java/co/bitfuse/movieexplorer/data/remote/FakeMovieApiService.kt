package co.bitfuse.movieexplorer.data.remote

import co.bitfuse.movieexplorer.core.network.NetworkConfig
import co.bitfuse.movieexplorer.data.model.MovieDto
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeMovieApiService @Inject constructor(
    private val networkConfig: NetworkConfig,
) : MovieApiService {

    override suspend fun getTrendingMovies(): List<MovieDto> {
        simulateNetwork()
        return hardcodedMovies
    }

    override suspend fun searchMovies(query: String): List<MovieDto> {
        simulateNetwork(delayMillis = 500)
        val normalizedQuery = query.trim()
        if (normalizedQuery.isBlank()) return emptyList()

        return hardcodedMovies.filter { movie ->
            movie.title.contains(normalizedQuery, ignoreCase = true) ||
                movie.genres.any { genre -> genre.contains(normalizedQuery, ignoreCase = true) }
        }
    }

    override suspend fun getMovieDetails(id: Int): MovieDto {
        simulateNetwork(delayMillis = 600)
        return hardcodedMovies.firstOrNull { it.id == id }
            ?: throw IllegalArgumentException("Movie with id $id was not found.")
    }

    private suspend fun simulateNetwork(delayMillis: Long = 1_000) {
        delay(delayMillis)
        if (shouldThrowError()) {
            throw IllegalStateException("Could not load movies. Please try again.")
        }
    }

    private fun shouldThrowError(): Boolean = networkConfig.simulateErrors

    private val hardcodedMovies = listOf(
        MovieDto(
            id = 1,
            title = "Aurora Protocol",
            overview = "A rescue pilot discovers a hidden signal that could stop a city-wide blackout.",
            posterUrl = "https://picsum.photos/seed/aurora-protocol/500/750",
            backdropUrl = "https://picsum.photos/seed/aurora-backdrop/1200/675",
            rating = 8.2,
            releaseYear = "2026",
            genres = listOf("Sci-Fi", "Thriller"),
        ),
        MovieDto(
            id = 2,
            title = "The Last Lantern",
            overview = "Two siblings cross a flooded coast to return a mysterious lantern to its keeper.",
            posterUrl = "https://picsum.photos/seed/last-lantern/500/750",
            backdropUrl = "https://picsum.photos/seed/lantern-backdrop/1200/675",
            rating = 7.8,
            releaseYear = "2025",
            genres = listOf("Adventure", "Drama"),
        ),
        MovieDto(
            id = 3,
            title = "Midnight Kitchen",
            overview = "A burned-out chef turns a closed diner into a secret midnight supper club.",
            posterUrl = "https://picsum.photos/seed/midnight-kitchen/500/750",
            backdropUrl = "https://picsum.photos/seed/kitchen-backdrop/1200/675",
            rating = 7.4,
            releaseYear = "2024",
            genres = listOf("Comedy", "Drama"),
        ),
        MovieDto(
            id = 4,
            title = "North Star Run",
            overview = "A courier racing across polar terrain becomes the only hope for a stranded research team.",
            posterUrl = "https://picsum.photos/seed/north-star-run/500/750",
            backdropUrl = "https://picsum.photos/seed/north-star-backdrop/1200/675",
            rating = 8.0,
            releaseYear = "2026",
            genres = listOf("Action", "Adventure"),
        ),
        MovieDto(
            id = 5,
            title = "Paper Moons",
            overview = "An illustrator finds that every moon she draws changes the dreams of her neighborhood.",
            posterUrl = "https://picsum.photos/seed/paper-moons/500/750",
            backdropUrl = "https://picsum.photos/seed/moons-backdrop/1200/675",
            rating = 7.6,
            releaseYear = "2023",
            genres = listOf("Fantasy", "Romance"),
        ),
        MovieDto(
            id = 6,
            title = "Static Hearts",
            overview = "A radio host and a skeptical engineer chase a strange broadcast across the desert.",
            posterUrl = "https://picsum.photos/seed/static-hearts/500/750",
            backdropUrl = "https://picsum.photos/seed/static-backdrop/1200/675",
            rating = 7.1,
            releaseYear = "2025",
            genres = listOf("Mystery", "Romance"),
        ),
        MovieDto(
            id = 7,
            title = "Harbor of Glass",
            overview = "A detective returns home when a quiet harbor town begins reflecting impossible memories.",
            posterUrl = "https://picsum.photos/seed/harbor-glass/500/750",
            backdropUrl = "https://picsum.photos/seed/harbor-backdrop/1200/675",
            rating = 8.4,
            releaseYear = "2026",
            genres = listOf("Mystery", "Drama"),
        ),
        MovieDto(
            id = 8,
            title = "Orbit Cafe",
            overview = "On a tiny orbital diner, regular customers prepare for Earth's first interstellar festival.",
            posterUrl = "https://picsum.photos/seed/orbit-cafe/500/750",
            backdropUrl = "https://picsum.photos/seed/orbit-backdrop/1200/675",
            rating = 7.9,
            releaseYear = "2024",
            genres = listOf("Sci-Fi", "Comedy"),
        ),
    )
}
