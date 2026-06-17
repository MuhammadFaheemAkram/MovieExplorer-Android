package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.testing.FakeMovieRepository
import com.example.movieexplorer.testing.testMovie
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchMoviesUseCaseTest {

    @Test
    fun `search delegates query to repository and returns movies`() = runTest {
        val repository = FakeMovieRepository().apply {
            searchResult = Result.success(listOf(testMovie(id = 8, title = "Orbit Cafe")))
        }
        val useCase = SearchMoviesUseCase(repository)

        val result = useCase("orbit")

        assertEquals("orbit", repository.lastSearchQuery)
        assertEquals("Orbit Cafe", result.single().title)
    }
}
