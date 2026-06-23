package co.bitfuse.movieexplorer.feature.search

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false,
    val movies: List<SearchMovieUiModel> = emptyList(),
    val errorMessage: String? = null,
)
