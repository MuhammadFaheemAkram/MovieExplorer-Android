package com.example.movieexplorer.testing

import com.example.movieexplorer.feature.home.MovieUiModel
import com.example.movieexplorer.feature.search.SearchMovieUiModel

fun testHomeMovieUiModel(
    id: Int = 1,
    title: String = "Aurora Protocol",
    isFavorite: Boolean = false,
): MovieUiModel = MovieUiModel(
    id = id,
    title = title,
    overview = "A rescue pilot discovers a hidden signal.",
    posterUrl = "https://picsum.photos/seed/home-test-$id/500/750",
    backdropUrl = "https://picsum.photos/seed/home-backdrop-test-$id/1200/675",
    rating = "8.2",
    releaseYear = "2026",
    genres = listOf("Sci-Fi", "Thriller"),
    isFavorite = isFavorite,
)

fun testSearchMovieUiModel(
    id: Int = 8,
    title: String = "Orbit Cafe",
): SearchMovieUiModel = SearchMovieUiModel(
    id = id,
    title = title,
    overview = "A tiny orbital diner prepares for a festival.",
    posterUrl = "https://picsum.photos/seed/search-test-$id/500/750",
    rating = "7.9",
    releaseYear = "2024",
    genres = listOf("Sci-Fi", "Comedy"),
)
