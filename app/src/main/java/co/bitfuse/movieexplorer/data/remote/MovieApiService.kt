package co.bitfuse.movieexplorer.data.remote

import co.bitfuse.movieexplorer.data.model.MovieRequestDto
import co.bitfuse.movieexplorer.data.model.MovieDto
import co.bitfuse.movieexplorer.data.model.MovieListResponseDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit description of the Movie Explorer REST API (FastAPI backend).
 * Paths are relative to `BuildConfig.API_BASE_URL` (e.g. http://10.0.2.2:8000/api/v1/).
 *
 * Retrofit generates the implementation; see `NetworkModule`.
 */
interface MovieApiService {

    /** `GET /movies?search=&limit=&offset=` - best rated first; `search` matches title or genre. */
    @GET("movies")
    suspend fun getMovies(
        @Query("search") search: String? = null,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
    ): MovieListResponseDto

    /** `GET /movies/{id}` - 404 if the movie does not exist. */
    @GET("movies/{id}")
    suspend fun getMovieDetails(@Path("id") id: Int): MovieDto

    /** `POST /movies` - 201 with the created movie, 409 for a duplicate title + year, 422 if invalid. */
    @POST("movies")
    suspend fun createMovie(@Body request: MovieRequestDto): MovieDto

    /** `PATCH /movies/{id}` - 200 with the updated movie, 404 if missing, 409 if it now duplicates another. */
    @PATCH("movies/{id}")
    suspend fun updateMovie(
        @Path("id") id: Int,
        @Body request: MovieRequestDto,
    ): MovieDto

    /** `DELETE /movies/{id}` - 204 No Content; its genre links and favorite are removed too. */
    @DELETE("movies/{id}")
    suspend fun deleteMovie(@Path("id") id: Int)

    /** `GET /favorites` - favorite movies, most recently added first. */
    @GET("favorites")
    suspend fun getFavoriteMovies(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int = 0,
    ): MovieListResponseDto

    /** `PUT /favorites/{id}` - idempotent: adding an existing favorite is not an error. */
    @PUT("favorites/{id}")
    suspend fun addFavorite(@Path("id") movieId: Int): MovieDto

    /** `DELETE /favorites/{id}` - idempotent; the backend answers 204 No Content. */
    @DELETE("favorites/{id}")
    suspend fun removeFavorite(@Path("id") movieId: Int)
}
