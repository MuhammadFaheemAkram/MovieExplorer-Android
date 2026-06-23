package co.bitfuse.movieexplorer.feature.home

import co.bitfuse.movieexplorer.domain.usecase.GetTrendingMoviesUseCase
import co.bitfuse.movieexplorer.domain.usecase.RefreshTrendingMoviesUseCase
import co.bitfuse.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import co.bitfuse.movieexplorer.testing.FakeMovieRepository
import co.bitfuse.movieexplorer.testing.MainDispatcherRule
import co.bitfuse.movieexplorer.testing.testMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `initial load exposes success state`() = runTest {
        val repository = FakeMovieRepository().apply {
            refreshResult = Result.success(listOf(testMovie(title = "Aurora Protocol")))
        }
        val viewModel = homeViewModel(repository)
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Aurora Protocol", state.movies.single().title)
        assertEquals(null, state.errorMessage)

        collectJob.cancel()
    }

    @Test
    fun `initial load exposes error state`() = runTest {
        val repository = FakeMovieRepository().apply {
            refreshResult = Result.failure(IllegalStateException("Network failed"))
        }
        val viewModel = homeViewModel(repository)
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertEquals("Network failed", state.errorMessage)

        collectJob.cancel()
    }

    private fun homeViewModel(repository: FakeMovieRepository): HomeViewModel =
        HomeViewModel(
            getTrendingMoviesUseCase = GetTrendingMoviesUseCase(repository),
            refreshTrendingMoviesUseCase = RefreshTrendingMoviesUseCase(repository),
            toggleFavoriteMovieUseCase = ToggleFavoriteMovieUseCase(repository),
        )
}
