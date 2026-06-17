package com.example.movieexplorer.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.movieexplorer.core.designsystem.MovieExplorerTestTags
import com.example.movieexplorer.core.designsystem.theme.MovieExplorerSpacing
import com.example.movieexplorer.domain.model.ContentLanguage

@Composable
fun SettingsRoute(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onDarkModeChange = viewModel::setDarkModeEnabled,
        onLanguageChange = viewModel::setContentLanguage,
        modifier = modifier,
    )
}

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onDarkModeChange: (Boolean) -> Unit,
    onLanguageChange: (ContentLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = MovieExplorerSpacing.small),
    ) {
        ListItem(
            headlineContent = { Text(text = "Dark mode") },
            supportingContent = { Text(text = if (uiState.darkModeEnabled) "On" else "Off") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.DarkMode,
                    contentDescription = null,
                )
            },
            trailingContent = {
                Switch(
                    checked = uiState.darkModeEnabled,
                    onCheckedChange = onDarkModeChange,
                    modifier = Modifier.testTag(MovieExplorerTestTags.SettingsDarkModeSwitch),
                )
            },
        )
        HorizontalDivider()
        LanguageSelector(
            selectedLanguage = uiState.selectedLanguage,
            availableLanguages = uiState.availableLanguages,
            onLanguageChange = onLanguageChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(MovieExplorerSpacing.medium),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSelector(
    selectedLanguage: ContentLanguage,
    availableLanguages: List<ContentLanguage>,
    onLanguageChange: (ContentLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MovieExplorerSpacing.small),
        ) {
            Icon(
                imageVector = Icons.Outlined.Language,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "Content language",
                style = MaterialTheme.typography.titleMedium,
            )
        }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedLanguage.label,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true,
                    )
                    .testTag(MovieExplorerTestTags.SettingsLanguageField)
                    .fillMaxWidth(),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                availableLanguages.forEach { language ->
                    DropdownMenuItem(
                        text = { Text(text = language.label) },
                        onClick = {
                            expanded = false
                            onLanguageChange(language)
                        },
                    )
                }
            }
        }
    }
}
