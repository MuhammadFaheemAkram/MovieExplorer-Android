# Phase 4 Summary: Search, Settings, Testing, And Polish

## Phase Goal

Complete the app with debounced search, persisted settings, app-wide theme updates, broader automated tests, and repository-ready documentation/testing polish.

## What Was Implemented

- Debounced search query flow.
- Search loading, empty-query, no-results, error, and result states.
- Search result navigation to Details.
- Preferences DataStore named `user_settings`.
- Dark-mode and content-language preferences.
- Settings repository and use cases.
- `SettingsViewModel` and stateless Settings UI.
- `MainViewModel` and immediate app-wide dark-theme updates.
- JVM tests for use cases, Home/Search/Details ViewModels, and DataStore repository.
- Compose UI tests for Home, Search, and Settings.
- Stable UI test tags.
- Contributor/testing documentation and final learning documentation.

The stored content-language preference is not full localization; strings remain hard-coded English text.

## Android Concepts Demonstrated

- Flow debounce, `distinctUntilChanged`, and `collectLatest`.
- Deterministic coroutine testing with virtual time.
- Preferences DataStore.
- App-level settings observation.
- Material 3 switch and exposed dropdown menu.
- Stateless Compose UI testing.
- Semantics test tags.
- Repository fakes.
- Documentation as part of project completion.

## Data Flow

Search:

```text
Search text field
  -> SearchViewModel.onQueryChange()
  -> query StateFlow
  -> trim + debounce(350) + distinctUntilChanged
  -> collectLatest
  -> SearchMoviesUseCase
  -> MovieRepository
  -> FakeMovieApiService.searchMovies()
  -> SearchUiState
  -> SearchScreen
```

Dark mode:

```text
Settings switch
  -> SettingsViewModel
  -> UpdateThemeUseCase
  -> SettingsRepository
  -> DataStore edit
  -> ObserveSettingsUseCase
  -> MainViewModel StateFlow
  -> MainActivity
  -> MovieExplorerTheme
```

## Important Files

- `feature/search/SearchViewModel.kt`: Debounced query processing and state transitions.
- `feature/search/SearchUiState.kt`: Search state contract.
- `feature/search/SearchScreen.kt`: Search input and all result states.
- `data/repository/DefaultSettingsRepository.kt`: DataStore mapping and writes.
- `domain/repository/SettingsRepository.kt`: Settings contract.
- `domain/model/UserSettings.kt`: Persisted settings model.
- `domain/model/ContentLanguage.kt`: Supported preference values.
- `feature/settings/SettingsViewModel.kt`: Settings state/actions.
- `feature/settings/SettingsScreen.kt`: Dark-mode switch and language dropdown.
- `MainViewModel.kt`: App-level settings StateFlow.
- `MainActivity.kt`: Applies persisted dark mode to the root theme.
- `core/designsystem/MovieExplorerTestTags.kt`: Stable UI-test semantics tags.
- `app/src/test/...`: JVM tests and fakes.
- `app/src/androidTest/...`: Compose UI tests.

## Important Classes

### `SearchViewModel`

Owns query state and uses Flow timing operators. `collectLatest` prevents old searches from winning after a newer query.

### `DefaultSettingsRepository`

Converts raw preference keys into a typed `UserSettings` Flow and provides transactional edits.

### `SettingsViewModel`

Maps domain settings into UI state and exposes explicit event functions for theme/language changes.

### `MainViewModel`

Lifts the settings Flow above navigation so theme changes affect the entire app immediately.

### `MainDispatcherRule` And `FakeMovieRepository`

Provide deterministic coroutine timing and replace production data sources during local tests.

## Interview Notes

### Why debounce search in the ViewModel?

It is application behavior, not rendering behavior. Keeping it in the ViewModel prevents duplicate UI implementations and makes timing testable.

### Why use `collectLatest` after debounce?

If a new query arrives while the previous search is still running, the previous block is cancelled so stale results do not overwrite the latest query.

### Why observe theme state above the navigation host?

The Material theme wraps the whole app. Observing at activity/root level lets every destination update together.

### Why test stateless screen composables directly?

Tests can provide exact UI state and verify output/callbacks without Hilt, Room, navigation, or network setup.

### What is the difference between compiling and running UI tests?

`assembleDebugAndroidTest` proves the test APK compiles. `connectedDebugAndroidTest` actually executes tests on an emulator or device.

## Learning Checklist

- [x] Debounce a StateFlow query.
- [x] Cancel stale work with `collectLatest`.
- [x] Render empty-query and no-results as different states.
- [x] Create a Preferences DataStore.
- [x] Hide DataStore behind a repository.
- [x] Update a root Compose theme from persisted state.
- [x] Test coroutine debounce with virtual time.
- [x] Test ViewModel success/error transitions.
- [x] Test a real temporary DataStore.
- [x] Render stateless composables in UI tests.
- [x] Add stable test tags and contributor documentation.

## Future Improvements

- Apply the language setting to localized resources.
- Add Settings/Favorites/Main ViewModel tests.
- Add navigation and Room tests.
- Add snapshot testing.
- Add CI to run build, unit tests, lint, and test APK compilation.
- Add real screenshots and choose a repository license.
