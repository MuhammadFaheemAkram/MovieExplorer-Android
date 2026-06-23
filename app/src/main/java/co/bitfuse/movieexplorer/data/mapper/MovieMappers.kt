package co.bitfuse.movieexplorer.data.mapper

import co.bitfuse.movieexplorer.data.model.MovieDto
import co.bitfuse.movieexplorer.data.local.MovieEntity
import co.bitfuse.movieexplorer.domain.model.Movie

// DTOs, entities, and domain models are separate so transport/storage changes do not leak upward.
fun MovieDto.toDomain(isFavorite: Boolean = false): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseYear = releaseYear,
    genres = genres,
    isFavorite = isFavorite,
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseYear = releaseYear,
    genres = genres.split(GENRE_SEPARATOR).filter { it.isNotBlank() },
    isFavorite = true,
)

fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating,
    releaseYear = releaseYear,
    genres = genres.joinToString(GENRE_SEPARATOR),
)

private const val GENRE_SEPARATOR = "|"
