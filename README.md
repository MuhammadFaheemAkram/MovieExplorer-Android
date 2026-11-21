# Movie Explorer

Movie Explorer is a learning-focused Android application that demonstrates how a small app can use production-inspired architecture without becoming over-engineered. It loads movies from a real REST backend (the `movie-explorer-api` project, FastAPI + PostgreSQL) through Retrofit, supports debounced search and movie details, stores favorites on that backend, and keeps app preferences in DataStore.

The project is intended to work as both a GitHub portfolio sample and a guided reference for beginner-to-intermediate Android developers.

## Main Features

- Browse a grid of trending movies.
- Pull to refresh movies from the backend.
- Search by movie title or genre with a 350 ms debounce.
- Open a details screen by navigation argument.
- Add and remove favorites.
- Add, edit, and delete movies (`POST`, `PATCH`, `DELETE /movies`) when **Settings > Manage movies** is on. These actions are hidden by default.
- Favorites are saved on the backend, so they survive reinstalls and are shared by every client of the API.
- Toggle dark mode and choose a content-language preference.
- See loading, refreshing, success, empty, no-results, and error states.
- Navigate with a bottom bar, navigation drawer, and details back action.

## Tech Stack

- Kotlin
- Jetpack Compose and Material 3
- MVVM
- Coroutines, Flow, and StateFlow
- Hilt
- Retrofit, OkHttp, and kotlinx.serialization
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
  -> Retrofit MovieApiService (FastAPI backend) / DataStore
```

Data returns through repository flows or suspend results, ViewModels convert it into immutable UI state, and route composables collect that state with lifecycle awareness. Stateless screen composables receive state and callbacks.

See [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for the full explanation and diagrams.

## Screens And Modules

| Screen | Responsibility | Main learning points |
| --- | --- | --- |
| Home | Trending grid, refresh, favorite action | Combined flows, StateFlow, pull to refresh, UI states |
| Search | Query input and filtered results | Debounce, `collectLatest`, no-results and error states |
| Add / Edit Movie | One form for creating and editing a movie | Client-side validation, snapshot state for text fields, `SavedStateHandle` nav argument decides the mode, one-shot navigation |
| Details | Movie metadata, favorite action, Edit and Delete (when enabled) | Navigation arguments, sealed UI state, suspend loading |
| Favorites | Favorites stored on the backend | Refresh on subscription, idempotent PUT/DELETE, removal action |
| Settings | Dark mode, Manage movies switch, language preference | DataStore, app-level state, immediate theme updates |
| About | Project description | Reusable design-system placeholder component |

The app also includes shared navigation, theme, and dependency-injection packages under `core/` and `di/`.

## Folder Structure

```text
app/src/main/java/co/bitfuse/movieexplorer/
  core/
    common/          Result type
    designsystem/    Theme, spacing, reusable UI, test tags
    navigation/      Routes, navigation host, drawer and bottom bar
  data/
    mapper/          DTO-to-domain mappings
    model/           Remote DTOs (kotlinx.serialization)
    remote/          Retrofit API contract and error mapping
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
  di/                Hilt modules (NetworkModule builds Retrofit)
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

### Start the backend first

The app needs the Movie Explorer API running on your Mac:

```bash
cd ~/BitFuse/Python/movie-explorer-api && source .venv/bin/activate && fastapi dev app/main.py
```

See that project's README for first-time setup (database, migrations, seed data).

The base URL is a `BuildConfig` field set in `app/build.gradle.kts`:

| Where the app runs | Base URL |
| --- | --- |
| Android emulator (default) | `http://10.0.2.2:8000/api/v1/` (`10.0.2.2` is the emulator's alias for the Mac's `localhost`) |
| Physical phone on the same Wi-Fi | Start the backend with `--host 0.0.0.0`, then build with `-PmovieExplorerApiBaseUrl=http://<mac-ip>:8000/api/v1/` |

If the backend is not running, screens show "Could not reach the Movie Explorer server. Make sure the backend is running." 

## Backend API

`MovieApiService` is a Retrofit interface for the FastAPI backend:

| App action | Request |
| --- | --- |
| Trending list | `GET movies?limit=50` (best rated first) |
| Search | `GET movies?search={query}&limit=50` (title or genre) |
| Details | `GET movies/{id}` |
| Favorites list | `GET favorites?limit=100` (newest first) |
| Add favorite | `PUT favorites/{id}` |
| Remove favorite | `DELETE favorites/{id}` |
| Add movie | `POST movies` (201, or 409 for a duplicate title + year) |
| Edit movie | `PATCH movies/{id}` |
| Delete movie | `DELETE movies/{id}` (also removes its favorite) |

The backend seeds the same eight demo movies the old fake API had (ids 1 to 8), plus eight more. Poster and backdrop URLs use deterministic Picsum seeds and are loaded by Coil.

`safeApiCall` (in `data/remote/ApiCall.kt`) turns connection failures, HTTP errors, and bad JSON into a `MovieExplorerApiException` with a readable message. For HTTP errors it uses the backend's `{"detail": "..."}` text. Debug builds log every request and response body to Logcat (tag `okhttp.OkHttpClient`).

Plain `http://` is allowed only for local development hosts (`res/xml/network_security_config.xml`). Debug builds allow it for any host so a physical phone can reach the Mac.

## Offline And Local Storage

The app no longer has a local movie database. The backend is the single source of truth, and the repository keeps trending movies and favorites in in-memory `StateFlow`s so screens update immediately after a favorite toggle. Without the backend, the app shows error states. A Room cache for offline use is listed in the roadmap.

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

Current coverage includes use cases, Home/Search/Details ViewModels, `DefaultMovieRepository` (backed by a fake Retrofit service, including error mapping), the DataStore settings repository, and stateless Compose UI states for Home, Search, and Settings. Navigation end-to-end tests are not present yet.

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
- [Phase 5 summary: real backend](docs/PHASE_5_SUMMARY.md)
- [Phase 6 summary: add movie](docs/PHASE_6_SUMMARY.md)
- [Phase 7 summary: edit, delete, Manage movies switch](docs/PHASE_7_SUMMARY.md)
- [Contributing](CONTRIBUTING.md)

## Screenshots

| Home | Search | Details |
| --- | --- | --- |
| Add screenshot | Add screenshot | Add screenshot |

| Favorites | Settings | Dark theme |
| --- | --- | --- |
| Add screenshot | Add screenshot | Add screenshot |

## Roadmap

The next practical improvements are backend authentication with an admin role (the Manage movies switch only hides UI), a Room-backed offline cache, pagination, localization, CI, accessibility review, and repository screenshots. See [docs/ROADMAP.md](docs/ROADMAP.md) for a prioritized list.

## License

No license file has been selected yet. Add a `LICENSE` file before publishing the repository for external reuse or accepting contributions.
