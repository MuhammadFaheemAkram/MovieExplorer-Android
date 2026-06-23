package co.bitfuse.movieexplorer.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.MovieFilter
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen
import co.bitfuse.movieexplorer.core.designsystem.components.PlaceholderAction
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing

@Composable
fun SearchRoute(
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onClearQuery = viewModel::clearQuery,
        onRetry = viewModel::retry,
        onMovieClick = onMovieClick,
        modifier = modifier,
    )
}

@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(MovieExplorerSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
    ) {
        SearchField(
            query = uiState.query,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            modifier = Modifier.fillMaxWidth(),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when {
                uiState.query.isBlank() -> EmptyQueryState(modifier = Modifier.fillMaxSize())
                uiState.isLoading -> LoadingState(modifier = Modifier.fillMaxSize())
                uiState.errorMessage != null -> ErrorState(
                    message = uiState.errorMessage,
                    onRetry = onRetry,
                    modifier = Modifier.fillMaxSize(),
                )

                uiState.hasSearched && uiState.movies.isEmpty() -> NoResultsState(
                    query = uiState.query,
                    modifier = Modifier.fillMaxSize(),
                )

                else -> SearchResults(
                    movies = uiState.movies,
                    onMovieClick = onMovieClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.testTag(MovieExplorerTestTags.SearchField),
        singleLine = true,
        label = { Text(text = "Search movies") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = onClearQuery) {
                    Icon(
                        imageVector = Icons.Outlined.Clear,
                        contentDescription = "Clear search",
                    )
                }
            }
        },
    )
}

@Composable
private fun SearchResults(
    movies: List<SearchMovieUiModel>,
    onMovieClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.testTag(MovieExplorerTestTags.SearchResults),
        contentPadding = PaddingValues(bottom = MovieExplorerSpacing.large),
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
    ) {
        items(
            items = movies,
            key = { movie -> movie.id },
        ) { movie ->
            SearchResultRow(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
            )
        }
    }
}

@Composable
private fun SearchResultRow(
    movie: SearchMovieUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .testTag(MovieExplorerTestTags.SearchResultRow)
            .fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier = Modifier.padding(MovieExplorerSpacing.medium),
            horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
            verticalAlignment = Alignment.Top,
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = "${movie.title} poster",
                modifier = Modifier
                    .width(84.dp)
                    .aspectRatio(2f / 3f)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                    ),
                contentScale = ContentScale.Crop,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.tertiary,
                    )
                    Text(
                        text = movie.rating,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = movie.releaseYear,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = movie.genres.joinToString(" / "),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyQueryState(modifier: Modifier = Modifier) {
    MovieExplorerPlaceholderScreen(
        title = "Find a movie",
        subtitle = "Search by title or genre.",
        icon = Icons.Outlined.MovieFilter,
        modifier = modifier,
    )
}

@Composable
private fun NoResultsState(
    query: String,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "No results",
        subtitle = "No movies matched \"$query\".",
        icon = Icons.Outlined.SearchOff,
        modifier = modifier,
    )
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "Search failed",
        subtitle = message,
        icon = Icons.Outlined.Warning,
        modifier = modifier,
        action = PlaceholderAction(
            label = "Retry",
            onClick = onRetry,
        ),
    )
}
