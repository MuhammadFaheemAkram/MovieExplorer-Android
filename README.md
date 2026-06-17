# Movie Explorer

Movie Explorer is a learning-focused Android app that demonstrates a practical mid-level MVVM architecture with Jetpack Compose. It uses fake hard-coded movie data, but the project is structured like a production app with repositories, use cases, dependency injection, local persistence, settings storage, and testable ViewModels.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- MVVM
- Hilt
- Kotlin Coroutines
- Flow and StateFlow
- Room
- DataStore Preferences
- Navigation Compose
- Coil
- JUnit and kotlinx-coroutines-test

## Architecture

The app uses a clean-ish layered structure:

```text
app/src/main/java/com/example/movieexplorer/
  core/
    common/
    database/
    datastore/
    designsystem/
    navigation/
    network/
  data/
    local/
    mapper/
    model/
    remote/
    repository/
  domain/
    model/
    repository/
    usecase/
  feature/
    details/
    favorites/
    home/
    search/
    settings/
  di/
```

The important rule is that UI does not talk directly to network, Room, or DataStore. Composables receive state and callbacks. ViewModels expose immutable `StateFlow` UI state. Use cases represent screen-level actions. Repositories coordinate data sources.

## Data Flow

Home data flow:

```text
FakeMovieApiService
  -> DefaultMovieRepository
  -> GetTrendingMoviesUseCase / RefreshTrendingMoviesUseCase
  -> HomeViewModel
  -> HomeScreen
```

Favorites data flow:

```text
Home or Details favorite action
  -> ToggleFavoriteMovieUseCase
  -> DefaultMovieRepository
  -> MovieDao
  -> Room favorite_movies table
  -> FavoritesViewModel
  -> FavoritesScreen
```

Settings data flow:

```text
SettingsScreen
  -> SettingsViewModel
  -> UpdateThemeUseCase / UpdateContentLanguageUseCase
  -> DefaultSettingsRepository
  -> DataStore Preferences
  -> MainViewModel
  -> MovieExplorerTheme
```

## Screens

- Home teaches fake network loading, repository refresh, `StateFlow`, pull to refresh, loading/error/empty/success states, and favorite actions.
- Search teaches debounced Flow-based search, query state, loading/error/no-results states, and navigation to details.
- Details teaches navigation arguments, screen-specific sealed UI state, loading details by ID, and toggling favorites.
- Favorites teaches Room persistence, offline reads, list state, and removing saved movies.
- Settings teaches DataStore Preferences, app-level state, and theme updates from persisted settings.

## Fake Network

`FakeMovieApiService` behaves like a Retrofit-style service:

- `getTrendingMovies()`
- `searchMovies(query)`
- `getMovieDetails(id)`

It simulates network delay and has a `NetworkConfig.simulateErrors` flag that can be switched for error-state testing.

## Testing

Current tests cover:

- Search use case
- Toggle favorite use case
- HomeViewModel success state
- HomeViewModel error state
- SearchViewModel debounce and error states
- DetailsViewModel loading and favorite state
- DataStore-backed settings repository behavior
- Stateless Compose UI states for Home, Search, and Settings

Run tests:

```bash
./gradlew :app:testDebugUnitTest
```

Compile Compose UI tests:

```bash
./gradlew :app:assembleDebugAndroidTest
```

More testing notes are in [docs/TESTING.md](docs/TESTING.md).

Build the app:

```bash
./gradlew :app:assembleDebug
```

## Future Improvements

- Add Compose UI tests for navigation and screen states.
- Add Room migration tests once database versions change.
- Add a real Retrofit API implementation behind the existing `MovieApiService`.
- Add paging for larger result sets.
- Add cached trending movies beyond favorites.
- Add richer theme options such as system theme mode.
- Add localization resources for the selected content language.
