package co.bitfuse.movieexplorer.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * One movie exactly as the Movie Explorer backend sends it (`MovieResponse` in
 * the FastAPI project). Property names match the JSON keys (camelCase), so no
 * `@SerialName` annotations are needed.
 *
 * Extra keys sent by the backend (`createdAt`, `updatedAt`) are ignored because
 * the Json instance is configured with `ignoreUnknownKeys = true`.
 */
@Serializable
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: Int,
    val genres: List<String>,
    val isFavorite: Boolean = false,
)

/**
 * Body of `POST /movies` (create) and `PATCH /movies/{id}` (update). The backend
 * validates it again (and answers 409 for a duplicate title + year), so client-side
 * validation is a convenience, not a guarantee. PATCH accepts partial bodies, but
 * sending every field keeps the app simple: the form always has all of them.
 */
@Serializable
data class MovieRequestDto(
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: Double,
    val releaseYear: Int,
    val genres: List<String>,
)

/**
 * Paginated list returned by `GET /movies` and `GET /favorites`:
 * `{"items": [...], "total": 16, "limit": 20, "offset": 0}`.
 */
@Serializable
data class MovieListResponseDto(
    val items: List<MovieDto>,
    val total: Int,
    val limit: Int,
    val offset: Int,
)

/**
 * Error body returned by the backend: `{"detail": ...}`.
 *
 * `detail` is a plain string for our own errors (404, 409), but a list of
 * field errors for validation failures (422), so it is kept as a raw [JsonElement].
 */
@Serializable
data class ApiErrorDto(
    val detail: JsonElement? = null,
)
