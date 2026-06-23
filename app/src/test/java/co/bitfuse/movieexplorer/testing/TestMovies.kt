package co.bitfuse.movieexplorer.testing

import co.bitfuse.movieexplorer.domain.model.Movie

fun testMovie(
    id: Int = 1,
    title: String = "Aurora Protocol",
    isFavorite: Boolean = false,
): Movie = Movie(
    id = id,
    title = title,
    overview = "A practical test movie.",
    posterUrl = "https://picsum.photos/seed/test-poster-$id/500/750",
    backdropUrl = "https://picsum.photos/seed/test-backdrop-$id/1200/675",
    rating = 8.2,
    releaseYear = "2026",
    genres = listOf("Sci-Fi", "Thriller"),
    isFavorite = isFavorite,
)
