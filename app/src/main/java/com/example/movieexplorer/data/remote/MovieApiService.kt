package com.example.movieexplorer.data.remote

import com.example.movieexplorer.data.model.MovieDto

interface MovieApiService {
    suspend fun getTrendingMovies(): List<MovieDto>
    suspend fun searchMovies(query: String): List<MovieDto>
    suspend fun getMovieDetails(id: Int): MovieDto
}
