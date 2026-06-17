package com.example.movieexplorer.feature.details

import com.example.movieexplorer.domain.usecase.GetMovieDetailsUseCase
import com.example.movieexplorer.domain.usecase.ToggleFavoriteMovieUseCase
import com.example.movieexplorer.testing.FakeMovieRepository
import com.example.movieexplorer.testing.MainDispatcherRule
import com.example.movieexplorer.testing.testMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
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

    private fun detailsViewModel(repository: FakeMovieRepository): DetailsViewModel =
        DetailsViewModel(
            getMovieDetailsUseCase = GetMovieDetailsUseCase(repository),
            toggleFavoriteMovieUseCase = ToggleFavoriteMovieUseCase(repository),
        )
}
