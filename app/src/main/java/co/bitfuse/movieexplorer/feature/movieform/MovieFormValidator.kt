package co.bitfuse.movieexplorer.feature.movieform

import co.bitfuse.movieexplorer.domain.model.MovieDraft
import kotlin.math.absoluteValue

enum class MovieFormField {
    Title,
    Overview,
    Rating,
    ReleaseYear,
    Genres,
    PosterUrl,
    BackdropUrl,
}

/** Raw text exactly as typed; everything is a String until validation succeeds. */
data class MovieForm(
    val title: String = "",
    val overview: String = "",
    val rating: String = "",
    val releaseYear: String = "",
    val genres: String = "",
    val posterUrl: String = "",
    val backdropUrl: String = "",
)

sealed interface MovieFormValidationResult {
    data class Valid(val movieDraft: MovieDraft) : MovieFormValidationResult
    data class Invalid(val errors: Map<MovieFormField, String>) : MovieFormValidationResult
}

/**
 * Mirrors the backend rules (`MovieCreate` schema in movie-explorer-api) so users get
 * instant, field-level feedback instead of a round trip. The backend still validates
 * everything again; this is a convenience, not the source of truth.
 */
object MovieFormValidator {

    const val TITLE_MAX_LENGTH = 200
    const val OVERVIEW_MAX_LENGTH = 5_000
    const val URL_MAX_LENGTH = 500
    const val GENRE_MAX_LENGTH = 50
    const val MAX_GENRES = 10
    const val MIN_RATING = 0.0
    const val MAX_RATING = 10.0
    const val MIN_YEAR = 1888 // the first film ever recorded
    const val MAX_YEAR = 2100

    fun validate(form: MovieForm): MovieFormValidationResult {
        val errors = mutableMapOf<MovieFormField, String>()

        val title = form.title.trim()
        when {
            title.isEmpty() -> errors[MovieFormField.Title] = "Title is required."
            title.length > TITLE_MAX_LENGTH -> errors[MovieFormField.Title] = "Use at most $TITLE_MAX_LENGTH characters."
        }

        val overview = form.overview.trim()
        when {
            overview.isEmpty() -> errors[MovieFormField.Overview] = "Overview is required."
            overview.length > OVERVIEW_MAX_LENGTH -> errors[MovieFormField.Overview] = "Use at most $OVERVIEW_MAX_LENGTH characters."
        }

        // Accept "7,5" too, since some keyboards/locales use a comma as decimal separator.
        val rating = form.rating.trim().replace(',', '.').toDoubleOrNull()
        if (rating == null || rating < MIN_RATING || rating > MAX_RATING) {
            errors[MovieFormField.Rating] = "Enter a number from 0 to 10."
        }

        val releaseYear = form.releaseYear.trim().toIntOrNull()
        if (releaseYear == null || releaseYear < MIN_YEAR || releaseYear > MAX_YEAR) {
            errors[MovieFormField.ReleaseYear] = "Enter a year from $MIN_YEAR to $MAX_YEAR."
        }

        val genres = parseGenres(form.genres)
        when {
            genres.size > MAX_GENRES -> errors[MovieFormField.Genres] = "Use at most $MAX_GENRES genres."
            genres.any { genre -> genre.length > GENRE_MAX_LENGTH } ->
                errors[MovieFormField.Genres] = "Each genre can have at most $GENRE_MAX_LENGTH characters."
        }

        val posterUrl = form.posterUrl.trim()
        urlError(posterUrl)?.let { message -> errors[MovieFormField.PosterUrl] = message }

        val backdropUrl = form.backdropUrl.trim()
        urlError(backdropUrl)?.let { message -> errors[MovieFormField.BackdropUrl] = message }

        if (errors.isNotEmpty() || rating == null || releaseYear == null) {
            return MovieFormValidationResult.Invalid(errors)
        }

        val imageSeed = imageSeedFor(title)
        return MovieFormValidationResult.Valid(
            MovieDraft(
                title = title,
                overview = overview,
                posterUrl = posterUrl.ifEmpty { "https://picsum.photos/seed/$imageSeed/500/750" },
                backdropUrl = backdropUrl.ifEmpty { "https://picsum.photos/seed/$imageSeed-backdrop/1200/675" },
                rating = rating,
                releaseYear = releaseYear,
                genres = genres,
            ),
        )
    }

    /** "  sci-fi, Drama,, DRAMA " -> ["sci-fi", "Drama"] (trimmed, no blanks, no case-insensitive duplicates). */
    fun parseGenres(text: String): List<String> {
        val genres = mutableListOf<String>()
        text.split(',').forEach { rawGenre ->
            val genre = rawGenre.trim()
            val isDuplicate = genres.any { existing -> existing.equals(genre, ignoreCase = true) }
            if (genre.isNotEmpty() && !isDuplicate) {
                genres += genre
            }
        }
        return genres
    }

    // Empty is allowed: a placeholder image is generated instead.
    private fun urlError(url: String): String? = when {
        url.isEmpty() -> null
        url.length > URL_MAX_LENGTH -> "Use at most $URL_MAX_LENGTH characters."
        !url.startsWith("http://") && !url.startsWith("https://") -> "Start the link with http:// or https://"
        else -> null
    }

    // "The Last Lantern" -> "the-last-lantern". Picsum returns the same image for the same seed.
    private fun imageSeedFor(title: String): String {
        val slug = title.lowercase()
            .replace(Regex("[^a-z0-9]+"), "-")
            .trim('-')
        return slug.ifEmpty { "movie-${title.hashCode().absoluteValue}" }
    }
}
