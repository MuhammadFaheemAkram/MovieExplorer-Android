# Movie Explorer

Movie Explorer is a learning-focused Android application that demonstrates how a small app can use production-inspired architecture without becoming over-engineered. It loads hard-coded movie data through a fake API, supports debounced search and movie details, persists favorites with Room, and stores app preferences with DataStore.

The project is intended to work as both a GitHub portfolio sample and a guided reference for beginner-to-intermediate Android developers.

## Main Features

- Browse a grid of trending movies.
- Pull to refresh fake remote data.
- Search by movie title or genre with a 350 ms debounce.
- Open a details screen by navigation argument.
- Add and remove favorites.
- Read favorite movies from Room without a network request.
- Toggle dark mode and choose a content-language preference.
- See loading, refreshing, success, empty, no-results, and error states.
- Navigate with a bottom bar, navigation drawer, and details back action.

## Tech Stack

- Kotlin
- Jetpack Compose and Material 3
- MVVM
- Coroutines, Flow, and StateFlow
- Hilt
- Room
- DataStore Preferences
- Navigation Compose
- Coil
- JUnit 4 and kotlinx-coroutines-test
- Compose UI instrumentation tests

## Architecture Summary

The code uses a clean-ish layered architecture in one Android app module:

```text
Compose UI
  -> ViewModel
  -> Use case
  -> Repository interface
  -> Repository implementation
  -> Fake API / Room / DataStore
```

Data returns through repository flows or suspend results, ViewModels convert it into immutable UI state, and route composables collect that state with lifecycle awareness. Stateless screen composables receive state and callbacks.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the full explanation and diagrams.

## Screens And Modules

| Screen | Responsibility | Main learning points |
| --- | --- | --- |
| Home | Trending grid, refresh, favorite action | Combined flows, StateFlow, pull to refresh, UI states |
| Search | Query input and filtered results | Debounce, `collectLatest`, no-results and error states |
| Details | Movie metadata and favorite action | Navigation arguments, sealed UI state, suspend loading |
| Favorites | Offline list of saved movies | Room Flow, persistence, removal action |
| Settings | Dark mode and language preference | DataStore, app-level state, immediate theme updates |
| About | Project description | Reusable design-system placeholder component |

The app also includes shared navigation, theme, database, fake-network, and dependency-injection packages under `core/` and `di/`.

## Folder Structure

```text
app/src/main/java/com/example/movieexplorer/
  core/
    common/          Result type
    database/        Room database
    designsystem/    Theme, spacing, reusable UI, test tags
    navigation/      Routes, navigation host, drawer and bottom bar
    network/         Fake-network configuration
  data/
    local/           Room entity and DAO
    mapper/          DTO/entity/domain mappings
    model/           Remote DTO
    remote/          API contract and fake service
    repository/      Movie and settings repository implementations
  domain/
    model/           App-facing models
    repository/      Repository contracts
    usecase/         Focused application actions
  feature/
    home/
    search/
    favorites/
    details/
    settings/
  di/                Hilt modules
```

Tests are split between `app/src/test` for JVM tests and `app/src/androidTest` for Compose UI tests.

## How To Run

Requirements:

- Android Studio with the project SDK installed
- JDK compatible with the configured Android Gradle Plugin
- Android device or emulator running API 24 or newer

Build the debug APK:

```bash
./gradlew :app:assembleDebug
```

Install and run it from Android Studio, or install the APK from `app/build/outputs/apk/debug/` with ADB.

No API key, account, or backend is required.

## Fake API And Demo Data

`MovieApiService` looks like a real remote-service boundary, while `FakeMovieApiService` supplies eight hard-coded `MovieDto` records. It simulates latency with coroutine `delay`:

- Trending: 1000 ms
- Search: 500 ms
- Details: 600 ms

Search matches title or genre. Poster and backdrop URLs use deterministic Picsum seeds and are loaded by Coil. `NetworkConfig.simulateErrors` is `false` by default and can be changed in code when demonstrating error states.

## Offline And Local Storage

Room stores complete favorite movie rows in the `favorite_movies` table. This lets the Favorites screen and saved movie details work from local data. Trending movies are kept only in an in-memory repository cache, so the app is not fully offline-first.

DataStore Preferences persists:

- `dark_mode_enabled`
- `content_language`

The language selector currently stores a preference; it does not yet switch Android string resources or remote content.

## Testing

Run JVM tests:

```bash
./gradlew :app:testDebugUnitTest
```

Compile Compose UI tests:

```bash
./gradlew :app:assembleDebugAndroidTest
```

Run Compose UI tests on a connected emulator or device:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Current coverage includes use cases, Home/Search/Details ViewModels, the DataStore settings repository, and stateless Compose UI states for Home, Search, and Settings. Room DAO/migration tests and navigation end-to-end tests are not present yet.

See [docs/TESTING.md](docs/TESTING.md) for exact test files, techniques, and gaps.

## Documentation

- [Architecture](docs/ARCHITECTURE.md)
- [Testing guide](docs/TESTING.md)
- [Learning notes](docs/LEARNING_NOTES.md)
- [Interview notes](docs/INTERVIEW_NOTES.md)
- [Roadmap](docs/ROADMAP.md)
- [Phase 1 summary](docs/PHASE_1_SUMMARY.md)
- [Phase 2 summary](docs/PHASE_2_SUMMARY.md)
- [Phase 3 summary](docs/PHASE_3_SUMMARY.md)
- [Phase 4 summary](docs/PHASE_4_SUMMARY.md)
- [Contributing](CONTRIBUTING.md)

## Screenshots

| Home | Search | Details |
| --- | --- | --- |
| Add screenshot | Add screenshot | Add screenshot |

| Favorites | Settings | Dark theme |
| --- | --- | --- |
| Add screenshot | Add screenshot | Add screenshot |

## Roadmap

The next practical improvements are real API integration, Room-backed trending cache, pagination, localization, database migration tests, CI, accessibility review, and repository screenshots. See [docs/ROADMAP.md](docs/ROADMAP.md) for a prioritized list.

## License

No license file has been selected yet. Add a `LICENSE` file before publishing the repository for external reuse or accepting contributions.
