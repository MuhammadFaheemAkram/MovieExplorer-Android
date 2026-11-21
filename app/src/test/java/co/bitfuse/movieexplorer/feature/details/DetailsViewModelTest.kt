package co.bitfuse.movieexplorer.feature.details

import co.bitfuse.movieexplorer.domain.usecase.DeleteMovieUseCase
import co.bitfuse.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import co.bitfuse.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import co.bitfuse.movieexplorer.testing.FakeMovieRepository
import co.bitfuse.movieexplorer.testing.MainDispatcherRule
import co.bitfuse.movieexplorer.testing.testMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `load movie exposes success state`() = runTest {
        val repository = FakeMovieRepository().apply {
            trendingMovies.value = listOf(testMovie(id = 1, title = "Aurora Protocol"))
        }
        val viewModel = detailsViewModel(repository)

        viewModel.loadMovie(movieId = 1)
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertEquals("Aurora Protocol", state.movie.title)
    }

    @Test
    fun `toggle favorite updates details state and repository`() = runTest {
        val repository = FakeMovieRepository().apply {
            trendingMovies.value = listOf(testMovie(id = 1, title = "Aurora Protocol"))
        }
        val viewModel = detailsViewModel(repository)

        viewModel.loadMovie(movieId = 1)
        advanceUntilIdle()
        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertTrue(state.movie.isFavorite)
        assertEquals("Aurora Protocol", repository.favoriteMovies.value.single().title)
    }

    @Test
    fun `failed toggle keeps the previous favorite state`() = runTest {
        val repository = FakeMovieRepository().apply {
            trendingMovies.value = listOf(testMovie(id = 1, isFavorite = false))
            toggleFavoriteError = IllegalStateException("Could not reach the server.")
        }
        val viewModel = detailsViewModel(repository)

        viewModel.loadMovie(movieId = 1)
        advanceUntilIdle()
        viewModel.toggleFavorite()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertFalse(state.movie.isFavorite)
    }

    @Test
    fun `delete removes the movie and moves to deleted state`() = runTest {
        val repository = FakeMovieRepository().apply {
            trendingMovies.value = listOf(testMovie(id = 1))
        }
        val viewModel = detailsViewModel(repository)

        viewModel.loadMovie(movieId = 1)
        advanceUntilIdle()
        viewModel.deleteMovie()
        advanceUntilIdle()

        assertEquals(MovieDetailsUiState.Deleted, viewModel.uiState.value)
        assertEquals(listOf(1), repository.deletedMovieIds)
    }

    @Test
    fun `failed delete keeps the movie and shows the error`() = runTest {
        val repository = FakeMovieRepository().apply {
            trendingMovies.value = listOf(testMovie(id = 1))
            deleteMovieError = IllegalStateException("Could not reach the server.")
        }
        val viewModel = detailsViewModel(repository)

        viewModel.loadMovie(movieId = 1)
        advanceUntilIdle()
        viewModel.deleteMovie()
        advanceUntilIdle()

        val state = viewModel.uiState.value as MovieDetailsUiState.Success
        assertFalse(state.isDeleting)
        assertEquals("Could not reach the server.", state.actionErrorMessage)
    }

    private fun detailsViewModel(repository: FakeMovieRepository): DetailsViewModel =
        DetailsViewModel(
            getMovieDetailsUseCase = GetMovieDetailsUseCase(repository),
            toggleFavoriteMovieUseCase = ToggleFavoriteMovieUseCase(repository),
            deleteMovieUseCase = DeleteMovieUseCase(repository),
        )
}
