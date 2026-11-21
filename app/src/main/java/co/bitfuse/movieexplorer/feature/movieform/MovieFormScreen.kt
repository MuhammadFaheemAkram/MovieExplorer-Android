package co.bitfuse.movieexplorer.feature.movieform

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import co.bitfuse.movieexplorer.core.designsystem.MovieExplorerTestTags
import co.bitfuse.movieexplorer.core.designsystem.components.MovieExplorerPlaceholderScreen
import co.bitfuse.movieexplorer.core.designsystem.components.PlaceholderAction
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerSpacing
import co.bitfuse.movieexplorer.core.designsystem.theme.MovieExplorerTheme

@Composable
fun MovieFormRoute(
    onMovieSaved: (movieId: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MovieFormViewModel = hiltViewModel(),
) {
    // Snapshot state: reading it here makes this composable recompose on every change.
    val uiState = viewModel.uiState

    // Navigate once, when the backend has returned the saved movie's id.
    LaunchedEffect(uiState.savedMovieId) {
        val movieId = uiState.savedMovieId ?: return@LaunchedEffect
        onMovieSaved(movieId)
        viewModel.onSavedMovieHandled()
    }

    MovieFormScreen(
        uiState = uiState,
        onFieldChange = viewModel::onFieldChange,
        onSave = viewModel::save,
        onRetryLoad = viewModel::loadMovie,
        modifier = modifier,
    )
}

@Composable
fun MovieFormScreen(
    uiState: MovieFormUiState,
    onFieldChange: (MovieFormField, String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
    onRetryLoad: () -> Unit = {},
) {
    when {
        // Edit mode: wait for the existing movie before showing the form.
        uiState.isLoading -> Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }

        uiState.loadErrorMessage != null -> MovieExplorerPlaceholderScreen(
            title = "Movie could not load",
            subtitle = uiState.loadErrorMessage,
            icon = Icons.Outlined.Warning,
            modifier = modifier.fillMaxSize(),
            action = PlaceholderAction(label = "Retry", onClick = onRetryLoad),
        )

        else -> MovieFormContent(
            uiState = uiState,
            onFieldChange = onFieldChange,
            onSave = onSave,
            modifier = modifier,
        )
    }
}

@Composable
private fun MovieFormContent(
    uiState: MovieFormUiState,
    onFieldChange: (MovieFormField, String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val form = uiState.form
    val enabled = !uiState.isSaving

    Column(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(MovieExplorerSpacing.medium),
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
    ) {
        FormTextField(
            field = MovieFormField.Title,
            label = "Title",
            value = form.title,
            uiState = uiState,
            onFieldChange = onFieldChange,
            testTag = MovieExplorerTestTags.MovieFormTitleField,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
        )
        FormTextField(
            field = MovieFormField.Overview,
            label = "Overview",
            value = form.overview,
            uiState = uiState,
            onFieldChange = onFieldChange,
            testTag = MovieExplorerTestTags.MovieFormOverviewField,
            singleLine = false,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small)) {
            FormTextField(
                field = MovieFormField.Rating,
                label = "Rating (0-10)",
                value = form.rating,
                uiState = uiState,
                onFieldChange = onFieldChange,
                testTag = MovieExplorerTestTags.MovieFormRatingField,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.weight(1f),
            )
            FormTextField(
                field = MovieFormField.ReleaseYear,
                label = "Release year",
                value = form.releaseYear,
                uiState = uiState,
                onFieldChange = onFieldChange,
                testTag = MovieExplorerTestTags.MovieFormYearField,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                modifier = Modifier.weight(1f),
            )
        }
        FormTextField(
            field = MovieFormField.Genres,
            label = "Genres",
            value = form.genres,
            uiState = uiState,
            onFieldChange = onFieldChange,
            testTag = MovieExplorerTestTags.MovieFormGenresField,
            hint = "Comma separated, e.g. Sci-Fi, Thriller",
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Next,
            ),
        )
        FormTextField(
            field = MovieFormField.PosterUrl,
            label = "Poster image URL",
            value = form.posterUrl,
            uiState = uiState,
            onFieldChange = onFieldChange,
            testTag = MovieExplorerTestTags.MovieFormPosterField,
            hint = "Optional. Leave empty for a placeholder image.",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Next),
        )
        FormTextField(
            field = MovieFormField.BackdropUrl,
            label = "Backdrop image URL",
            value = form.backdropUrl,
            uiState = uiState,
            onFieldChange = onFieldChange,
            testTag = MovieExplorerTestTags.MovieFormBackdropField,
            hint = "Optional. Leave empty for a placeholder image.",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri, imeAction = ImeAction.Done),
        )

        if (uiState.errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(MovieExplorerTestTags.MovieFormError),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                ),
            ) {
                Text(
                    text = uiState.errorMessage,
                    modifier = Modifier.padding(MovieExplorerSpacing.medium),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        Button(
            onClick = onSave,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MovieExplorerSpacing.small)
                .testTag(MovieExplorerTestTags.MovieFormSaveButton),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
            ) {
                if (uiState.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                    )
                }
                val label = when {
                    uiState.isSaving -> "Saving..."
                    uiState.isEditing -> "Save changes"
                    else -> "Save movie"
                }
                Text(text = label)
            }
        }
    }
}

/** One labelled field that shows its validation error (or a hint) under the input. */
@Composable
private fun FormTextField(
    field: MovieFormField,
    label: String,
    value: String,
    uiState: MovieFormUiState,
    onFieldChange: (MovieFormField, String) -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
) {
    val error = uiState.fieldErrors[field]
    val supportingText = error ?: hint

    OutlinedTextField(
        value = value,
        onValueChange = { newValue -> onFieldChange(field, newValue) },
        label = { Text(text = label) },
        isError = error != null,
        supportingText = supportingText?.let { text -> { Text(text = text) } },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else 3,
        maxLines = if (singleLine) 1 else 6,
        enabled = !uiState.isSaving,
        keyboardOptions = keyboardOptions,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
    )
}

@Preview(showBackground = true)
@Composable
private fun MovieFormScreenPreview() {
    MovieExplorerTheme {
        MovieFormScreen(
            uiState = MovieFormUiState(
                form = MovieForm(title = "Lantern Sea", rating = "11"),
                fieldErrors = mapOf(MovieFormField.Rating to "Enter a number from 0 to 10."),
            ),
            onFieldChange = { _, _ -> },
            onSave = {},
        )
    }
}
