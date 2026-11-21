package co.bitfuse.movieexplorer.feature.movieform

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieFormValidatorTest {

    private val validForm = MovieForm(
        title = "  Lantern Sea ",
        overview = "A lighthouse keeper follows floating lanterns.",
        rating = "7,5",
        releaseYear = "2026",
        genres = " Adventure, fantasy,, FANTASY ",
    )

    @Test
    fun `valid form is trimmed and parsed`() {
        val result = MovieFormValidator.validate(validForm) as MovieFormValidationResult.Valid

        with(result.movieDraft) {
            assertEquals("Lantern Sea", title)
            assertEquals(7.5, rating, 0.0)
            assertEquals(2026, releaseYear)
            assertEquals(listOf("Adventure", "fantasy"), genres)
        }
    }

    @Test
    fun `empty image urls become deterministic placeholders`() {
        val result = MovieFormValidator.validate(validForm) as MovieFormValidationResult.Valid

        assertEquals("https://picsum.photos/seed/lantern-sea/500/750", result.movieDraft.posterUrl)
        assertEquals("https://picsum.photos/seed/lantern-sea-backdrop/1200/675", result.movieDraft.backdropUrl)
    }

    @Test
    fun `empty form reports every required field`() {
        val result = MovieFormValidator.validate(MovieForm()) as MovieFormValidationResult.Invalid

        assertEquals(
            setOf(MovieFormField.Title, MovieFormField.Overview, MovieFormField.Rating, MovieFormField.ReleaseYear),
            result.errors.keys,
        )
    }

    @Test
    fun `out of range values and bad urls are rejected`() {
        val form = validForm.copy(
            rating = "10.5",
            releaseYear = "1700",
            posterUrl = "ftp://example.com/p.jpg",
            genres = "G".repeat(51),
        )

        val errors = (MovieFormValidator.validate(form) as MovieFormValidationResult.Invalid).errors

        assertTrue(MovieFormField.Rating in errors)
        assertTrue(MovieFormField.ReleaseYear in errors)
        assertTrue(MovieFormField.PosterUrl in errors)
        assertTrue(MovieFormField.Genres in errors)
    }

    @Test
    fun `more than ten genres is rejected`() {
        val genres = (1..11).joinToString(separator = ",") { number -> "Genre $number" }

        val errors = (MovieFormValidator.validate(validForm.copy(genres = genres)) as MovieFormValidationResult.Invalid).errors

        assertEquals("Use at most 10 genres.", errors[MovieFormField.Genres])
    }
}
