package com.example.movieexplorer.feature.search

import com.example.movieexplorer.domain.usecase.SearchMoviesUseCase
import com.example.movieexplorer.testing.FakeMovieRepository
import com.example.movieexplorer.testing.MainDispatcherRule
import com.example.movieexplorer.testing.testMovie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `query is debounced before search runs`() = runTest {
        val repository = FakeMovieRepository().apply {
            searchResult = Result.success(listOf(testMovie(id = 8, title = "Orbit Cafe")))
        }
        val viewModel = SearchViewModel(SearchMoviesUseCase(repository))

        advanceUntilIdle()
        viewModel.onQueryChange("or")
        advanceTimeBy(200)
        assertEquals(null, repository.lastSearchQuery)

        viewModel.onQueryChange("orbit")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("orbit", repository.lastSearchQuery)
        assertFalse(state.isLoading)
        assertEquals("Orbit Cafe", state.movies.single().title)
    }

    @Test
    fun `search error updates error state`() = runTest {
        val repository = FakeMovieRepository().apply {
            searchResult = Result.failure(IllegalStateException("Search unavailable"))
        }
        val viewModel = SearchViewModel(SearchMoviesUseCase(repository))

        advanceUntilIdle()
        viewModel.onQueryChange("aurora")
        advanceTimeBy(350)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertTrue(state.movies.isEmpty())
        assertEquals("Search unavailable", state.errorMessage)
    }
}
