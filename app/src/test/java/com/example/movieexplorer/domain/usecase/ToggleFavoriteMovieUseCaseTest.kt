package com.example.movieexplorer.domain.usecase

import com.example.movieexplorer.testing.FakeMovieRepository
import com.example.movieexplorer.testing.testMovie
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
