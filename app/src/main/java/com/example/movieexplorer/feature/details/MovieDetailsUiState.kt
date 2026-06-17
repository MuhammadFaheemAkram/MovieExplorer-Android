package com.example.movieexplorer.feature.details

sealed interface MovieDetailsUiState {
    data object Loading : MovieDetailsUiState
    data class Success(val movie: MovieDetailsUiModel) : MovieDetailsUiState
    data class Error(val message: String) : MovieDetailsUiState
}
