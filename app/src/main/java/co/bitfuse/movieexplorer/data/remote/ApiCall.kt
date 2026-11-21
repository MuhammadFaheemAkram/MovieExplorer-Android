package co.bitfuse.movieexplorer.data.remote

import co.bitfuse.movieexplorer.data.model.ApiErrorDto
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import retrofit2.HttpException
import java.io.IOException

/**
 * Network/server failure with a message that is safe to show in the UI.
 * ViewModels already display `throwable.message`, so they need no changes.
 */
class MovieExplorerApiException(
    message: String,
    val httpCode: Int? = null,
    cause: Throwable? = null,
) : Exception(message, cause)

private val errorJson = Json { ignoreUnknownKeys = true }

/**
 * Runs a Retrofit call and converts low-level failures into [MovieExplorerApiException]:
 * - [IOException]: no connection (backend not running, wrong URL, no network).
 * - [HttpException]: the backend answered 4xx/5xx; its `{"detail": "..."}` message is used.
 * - [SerializationException]: the JSON did not match the DTOs.
 *
 * Coroutine cancellation is intentionally not caught.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): T =
    try {
        block()
    } catch (exception: IOException) {
        throw MovieExplorerApiException(
            message = "Could not reach the Movie Explorer server. Make sure the backend is running.",
            cause = exception,
        )
    } catch (exception: HttpException) {
        throw MovieExplorerApiException(
            message = exception.toUserMessage(),
            httpCode = exception.code(),
            cause = exception,
        )
    } catch (exception: SerializationException) {
        throw MovieExplorerApiException(
            message = "The server sent data the app could not read.",
            cause = exception,
        )
    }

private fun HttpException.toUserMessage(): String {
    val errorBody = response()?.errorBody()?.string()
    val detail = errorBody
        ?.let { body -> runCatching { errorJson.decodeFromString<ApiErrorDto>(body) }.getOrNull() }
        ?.detail
    return detail?.toReadableText() ?: "Server error (HTTP ${code()}). Please try again."
}

/**
 * - 404 / 409: `"detail": "Movie with id 5 was not found."` -> used as is.
 * - 422: `"detail": [{"loc": ["body", "rating"], "msg": "Input should be ..."}]`
 *   -> "rating: Input should be ..." (first error only).
 */
private fun JsonElement.toReadableText(): String? = when (this) {
    is JsonPrimitive -> contentOrNull
    is JsonArray -> {
        val firstError = firstOrNull() as? JsonObject
        val message = (firstError?.get("msg") as? JsonPrimitive)?.contentOrNull
        val field = ((firstError?.get("loc") as? JsonArray)?.lastOrNull() as? JsonPrimitive)?.contentOrNull
        when {
            message == null -> null
            field == null -> message
            else -> "$field: $message"
        }
    }
    else -> null
}
