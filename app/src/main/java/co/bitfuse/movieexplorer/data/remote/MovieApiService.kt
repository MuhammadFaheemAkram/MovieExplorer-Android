package co.bitfuse.movieexplorer.data.remote

import co.bitfuse.movieexplorer.data.model.MovieDto

interface MovieApiService {
    suspend fun getTrendingMovies(): List<MovieDto>
    suspend fun searchMovies(query: String): List<MovieDto>
    suspend fun getMovieDetails(id: Int): MovieDto
}
