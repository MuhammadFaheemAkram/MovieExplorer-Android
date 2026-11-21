# Phase 5 Summary: Real Backend

## Goal

Replace the fake API and the local Room favorites table with the real **Movie Explorer API** (FastAPI + SQLAlchemy + PostgreSQL, in `~/BitFuse/Python/movie-explorer-api`), keeping the domain and UI layers untouched.

## What Changed

| Area | Before | After |
| --- | --- | --- |
| Movie data | `FakeMovieApiService` with 8 hard-coded DTOs and fake delays | Retrofit `MovieApiService` calling `http://10.0.2.2:8000/api/v1/` |
| Search | In-memory filter in the fake service | `GET movies?search=` (title or genre, case-insensitive, on the server) |
| Favorites | Room `favorite_movies` table | Backend `favorites` table via `PUT` / `DELETE favorites/{id}` |
| JSON | none | kotlinx.serialization DTOs (`MovieDto`, `MovieListResponseDto`, `ApiErrorDto`) |
| Errors | `NetworkConfig.simulateErrors` flag | `safeApiCall` → `MovieExplorerApiException` with a readable message |
| DI | `DataModule` bound the fake service; `DatabaseModule` built Room | `NetworkModule` builds OkHttp, Json, Retrofit, and the service |
| Config | none | `BuildConfig.API_BASE_URL` (overridable with `-PmovieExplorerApiBaseUrl=...`) |
| Cleartext HTTP | not needed | `network_security_config.xml` (emulator hosts in release, any host in debug) |

Removed: `FakeMovieApiService`, `NetworkConfig`, `MovieEntity`, `MovieDao`, `MovieExplorerDatabase`, `DatabaseModule`, and the Room dependencies.

## What Did Not Change (and why that matters)

`Movie`, the repository interface, every use case except one, all UI models, and all screens are unchanged. The backend sends `releaseYear` as a number while the UI shows a `String`; `MovieMappers.kt` converts it. This is the payoff of separating DTOs from domain models in earlier phases.

## Behaviour Changes

- `ToggleFavoriteMovieUseCase` now returns `MovieExplorerResult<Unit>`. A favorite toggle can fail over the network, and a thrown exception inside `viewModelScope.launch` would crash the app.
  - Home shows the failure in its existing inline error message.
  - Details only flips the heart after the backend confirms.
  - Favorites keeps the item if removal fails.
- The repository picks `PUT` or `DELETE` from the movie's current `isFavorite`. Both are idempotent on the backend, so retries are safe (a "toggle" endpoint would not be).
- Favorites are ordered newest first (backend order) instead of by title.
- The app needs the backend running. Without it, screens show "Could not reach the Movie Explorer server. Make sure the backend is running."

## Build Fix

`settings.gradle.kts` now limits the `google()` repository to `com.android.*`, `com.google.*`, and `androidx.*` groups (the same filter `pluginManagement` already had). Without it, Gradle asked Google's Maven for Retrofit first and failed when that server returned 502 instead of 404.

## Tests

- New `DefaultMovieRepositoryTest` (6 tests, fake Retrofit service): favorite flags merged into trending, add/remove toggling, blank and trimmed search, connection error mapping, and HTTP `detail` message mapping.
- `ToggleFavoriteMovieUseCaseTest` and `DetailsViewModelTest` gained failure-path tests (`FakeMovieRepository.toggleFavoriteError`).
- 17 JVM tests pass.
- Verified manually on the `Pixel_10_Pro` emulator: Home list, add favorite (`PUT` 200), Favorites screen, remove (`DELETE` 204), search, and the offline error state.

## Learning Points

- Debug builds log every HTTP call in Logcat (filter `okhttp.OkHttpClient`). Watch it while using the app, then compare with the backend's SQL log (`DEBUG=true` in the backend `.env`).
- `10.0.2.2` is the emulator's route to the host machine's `localhost`.
- A data-layer exception type with UI-safe messages keeps ViewModels unchanged.

## Next

- "Add movie" screen using `POST /movies` (the backend endpoint already exists).
- Paging 3 using the backend's `limit` / `offset` / `total`.
- Optional Room cache for offline viewing.
