package co.bitfuse.movieexplorer.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen
import co.bitfuse.movieexplorer.core.designsystem.components.PlaceholderAction
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing

@Composable
fun DetailsRoute(
    movieId: Int,
    // From the "Manage movies" setting: shows the Edit and Delete buttons.
    canManageMovies: Boolean,
    onEditClick: (movieId: Int) -> Unit,
    onMovieDeleted: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(movieId) {
        viewModel.loadMovie(movieId)
    }

    // Leave the screen once, after the backend confirmed the delete.
    LaunchedEffect(uiState) {
        if (uiState == MovieDetailsUiState.Deleted) {
            onMovieDeleted()
        }
    }

    DetailsScreen(
        uiState = uiState,
        onRetry = { viewModel.loadMovie(movieId) },
        onFavoriteClick = viewModel::toggleFavorite,
        canManageMovies = canManageMovies,
        onEditClick = { onEditClick(movieId) },
        onDeleteConfirmed = viewModel::deleteMovie,
        modifier = modifier,
    )
}

@Composable
fun DetailsScreen(
    uiState: MovieDetailsUiState,
    onRetry: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    canManageMovies: Boolean = false,
    onEditClick: () -> Unit = {},
    onDeleteConfirmed: () -> Unit = {},
) {
    when (uiState) {
        MovieDetailsUiState.Loading -> LoadingState(modifier = modifier.fillMaxSize())
        is MovieDetailsUiState.Error -> ErrorState(
            message = uiState.message,
            onRetry = onRetry,
            modifier = modifier.fillMaxSize(),
        )

        is MovieDetailsUiState.Success -> DetailsContent(
            uiState = uiState,
            onFavoriteClick = onFavoriteClick,
            canManageMovies = canManageMovies,
            onEditClick = onEditClick,
            onDeleteConfirmed = onDeleteConfirmed,
            modifier = modifier.fillMaxSize(),
        )

        // Shown for a moment while the route navigates back.
        MovieDetailsUiState.Deleted -> Box(modifier = modifier.fillMaxSize())
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun DetailsContent(
    uiState: MovieDetailsUiState.Success,
    onFavoriteClick: () -> Unit,
    canManageMovies: Boolean,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val movie = uiState.movie

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
    ) {
        item {
            Box {
                AsyncImage(
                    model = movie.backdropUrl,
                    contentDescription = "${movie.title} backdrop",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = MovieExplorerSpacing.large),
                verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.medium),
                    verticalAlignment = Alignment.Top,
                ) {
                    AsyncImage(
                        model = movie.posterUrl,
                        contentDescription = "${movie.title} poster",
                        modifier = Modifier
                            .fillMaxWidth(0.32f)
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
                            style = MaterialTheme.typography.titleLarge,
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
                        FavoriteActionChip(
                            isFavorite = movie.isFavorite,
                            onClick = onFavoriteClick,
                        )
                    }
                }

                if (canManageMovies) {
                    ManageMovieActions(
                        movieTitle = movie.title,
                        isDeleting = uiState.isDeleting,
                        errorMessage = uiState.actionErrorMessage,
                        onEditClick = onEditClick,
                        onDeleteConfirmed = onDeleteConfirmed,
                    )
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
                    verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
                ) {
                    movie.genres.forEach { genre ->
                        AssistChip(
                            onClick = {},
                            label = { Text(text = genre) },
                        )
                    }
                }

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(MovieExplorerSpacing.large))
            }
        }
    }
}

/** Edit and Delete buttons. Delete asks for confirmation first because it cannot be undone. */
@Composable
private fun ManageMovieActions(
    movieTitle: String,
    isDeleting: Boolean,
    errorMessage: String?,
    onEditClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small)) {
        Row(horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small)) {
            OutlinedButton(
                onClick = onEditClick,
                enabled = !isDeleting,
                modifier = Modifier
                    .weight(1f)
                    .testTag(MovieExplorerTestTags.DetailsEditButton),
            ) {
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = null)
                Spacer(modifier = Modifier.size(MovieExplorerSpacing.small))
                Text(text = "Edit")
            }
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                enabled = !isDeleting,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag(MovieExplorerTestTags.DetailsDeleteButton),
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(imageVector = Icons.Outlined.Delete, contentDescription = null)
                }
                Spacer(modifier = Modifier.size(MovieExplorerSpacing.small))
                Text(text = if (isDeleting) "Deleting..." else "Delete")
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "Delete movie?") },
            text = {
                Text(text = "\"$movieTitle\" will be removed for everyone, including from favorites. This cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    },
                    modifier = Modifier.testTag(MovieExplorerTestTags.DetailsDeleteConfirmButton),
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel")
                }
            },
        )
    }
}

@Composable
private fun FavoriteActionChip(
    isFavorite: Boolean,
    onClick: () -> Unit,
) {
    ElevatedAssistChip(
        onClick = onClick,
        label = {
            Text(text = if (isFavorite) "Remove favorite" else "Add favorite")
        },
        leadingIcon = {
            Icon(
                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = null,
            )
        },
    )
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
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MovieExplorerPlaceholderScreen(
        title = "Details could not load",
        subtitle = message,
        icon = Icons.Outlined.Warning,
        modifier = modifier,
        action = PlaceholderAction(
            label = "Retry",
            onClick = onRetry,
        ),
    )
}
