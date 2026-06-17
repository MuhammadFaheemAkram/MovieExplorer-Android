package com.example.movieexplorer.feature.home

import com.example.movieexplorer.domain.model.Movie

data class MovieUiModel(
    val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String,
    val backdropUrl: String,
    val rating: String,
    val releaseYear: String,
    val genres: List<String>,
    val isFavorite: Boolean,
)

fun Movie.toUiModel(): MovieUiModel = MovieUiModel(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = "%.1f".format(rating),
    releaseYear = releaseYear,
    genres = genres,
    isFavorite = isFavorite,
)

fun MovieUiModel.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterUrl = posterUrl,
    backdropUrl = backdropUrl,
    rating = rating.toDoubleOrNull() ?: 0.0,
    releaseYear = releaseYear,
    genres = genres,
    isFavorite = isFavorite,
)
