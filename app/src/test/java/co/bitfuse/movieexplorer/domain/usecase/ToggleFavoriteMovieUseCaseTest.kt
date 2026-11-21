package co.bitfuse.movieexplorer.domain.usecase

import co.bitfuse.movieexplorer.core.common.MovieExplorerResult
import co.bitfuse.movieexplorer.testing.FakeMovieRepository
import co.bitfuse.movieexplorer.testing.testMovie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ToggleFavoriteMovieUseCaseTest {

    @Test
    fun `toggle favorite adds then removes movie`() = runTest {
        val repository = FakeMovieRepository()
        val useCase = ToggleFavoriteMovieUseCase(repository)
        val movie = testMovie()

        val addResult = useCase(movie)
        assertEquals(MovieExplorerResult.Success(Unit), addResult)
        assertEquals(movie.copy(isFavorite = true), repository.favoriteMovies.value.single())

        useCase(movie)
        assertTrue(repository.favoriteMovies.value.isEmpty())
    }

    @Test
    fun `toggle favorite failure returns error instead of throwing`() = runTest {
        val repository = FakeMovieRepository().apply {
            toggleFavoriteError = IllegalStateException("Could not reach the server.")
        }
        val useCase = ToggleFavoriteMovieUseCase(repository)

        val result = useCase(testMovie())

        assertEquals("Could not reach the server.", (result as MovieExplorerResult.Error).message)
        assertTrue(repository.favoriteMovies.value.isEmpty())
    }
}
