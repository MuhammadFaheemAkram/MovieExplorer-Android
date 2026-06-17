package com.example.movieexplorer.domain.repository

import com.example.movieexplorer.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun observeTrendingMovies(): Flow<List<Movie>>
    fun observeFavoriteMovies(): Flow<List<Movie>>
    suspend fun refreshTrendingMovies()
    suspend fun searchMovies(query: String): List<Movie>
    suspend fun getMovieDetails(id: Int): Movie
    suspend fun toggleFavorite(movie: Movie)
}
