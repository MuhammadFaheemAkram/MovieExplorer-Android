package co.bitfuse.movieexplorer.domain.usecase

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

        useCase(movie)
        assertEquals(movie.copy(isFavorite = true), repository.favoriteMovies.value.single())

        useCase(movie)
        assertTrue(repository.favoriteMovies.value.isEmpty())
    }
}
