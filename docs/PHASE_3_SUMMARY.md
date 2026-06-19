# Phase 3 Summary: Room Favorites And Details

## Phase Goal

Add meaningful local persistence and complete the movie journey from a list item to details and offline favorites.

## What Was Implemented

- Room `MovieExplorerDatabase`.
- `MovieEntity` for the `favorite_movies` table.
- `MovieDao` queries, Flow observations, upsert, and delete.
- Entity/domain mappings.
- Hilt database and DAO providers.
- Repository refactor from in-memory favorite IDs to Room.
- Favorite ID Flow merged into Home and search movies.
- Full movie records saved for offline Favorites rendering.
- Details route loading by `movieId`.
- `DetailsViewModel` and sealed `MovieDetailsUiState`.
- Details backdrop, poster, metadata, genres, overview, and favorite action.
- `FavoritesViewModel`, UI state, offline list, empty state, and removal action.

## Android Concepts Demonstrated

- Room entity, DAO, and database.
- SQL validated at compile time.
- Reactive Room Flow queries.
- Suspend database reads/writes.
- Hilt providers for Android framework objects.
- Local/remote source coordination.
- Source precedence for details.
- Sealed UI state.
- Navigation argument consumption.
- Offline-capable feature design.

## Data Flow

Favorite toggle:

```text
Home or Details favorite button
  -> ViewModel
  -> ToggleFavoriteMovieUseCase
  -> DefaultMovieRepository
  -> MovieDao.isFavorite()
  -> upsert or delete MovieEntity
  -> Room Flow emits favorites/IDs
  -> Home and Favorites StateFlows update
  -> Compose recomposes
```

Details load:

```text
details/{movieId}
  -> DetailsRoute
  -> DetailsViewModel.loadMovie(id)
  -> GetMovieDetailsUseCase
  -> repository checks memory, then Room, then fake API
  -> MovieDetailsUiState.Success or Error
  -> DetailsScreen
```

## Important Files

- `core/database/MovieExplorerDatabase.kt`: Room database definition.
- `data/local/MovieEntity.kt`: Favorite table schema.
- `data/local/MovieDao.kt`: Favorite queries and updates.
- `data/mapper/MovieMappers.kt`: Entity/domain conversion.
- `di/DatabaseModule.kt`: Room and DAO providers.
- `data/repository/DefaultMovieRepository.kt`: Room integration and details precedence.
- `feature/details/DetailsViewModel.kt`: Details load and favorite actions.
- `feature/details/MovieDetailsUiState.kt`: Loading/success/error contract.
- `feature/details/MovieDetailsUiModel.kt`: Details presentation model.
- `feature/details/DetailsScreen.kt`: Details UI.
- `feature/favorites/FavoritesViewModel.kt`: Observes Room favorites.
- `feature/favorites/FavoritesScreen.kt`: Offline favorites list and empty state.

## Important Classes

### `MovieEntity`

Stores all movie fields needed to render Favorites and Details without another remote request. Genres are stored in one string.

### `MovieDao`

Exposes reactive list/ID queries and focused suspend commands for reads and writes.

### `DefaultMovieRepository`

Uses Room favorite IDs to decorate trending/search domain models and resolves details from memory, Room, or fake API.

### `DetailsViewModel`

Uses a sealed UI state because loading, success, and error are mutually exclusive screen modes.

### `FavoritesViewModel`

Maps the Room-backed favorites Flow into immutable UI state. Removing a favorite reuses the same toggle use case.

## Interview Notes

### Why store complete favorites rather than IDs?

Complete rows support offline rendering. With only IDs, the app would need remote data before showing saved movies.

### Why expose favorite IDs as a separate Flow?

The repository can efficiently merge favorite state into remote/cached lists without loading and comparing full entities.

### Why prefer Room over a manually managed SQLite database?

Room validates queries, generates adapters, supports coroutine/Flow APIs, and integrates with Android lifecycle architecture.

### What is missing for production database evolution?

Schema export and migration tests. The database is version 1 with `exportSchema = false`.

## Learning Checklist

- [x] Define a Room entity and primary key.
- [x] Write Flow and suspend DAO queries.
- [x] Provide Room through Hilt.
- [x] Map entity and domain models.
- [x] Persist complete favorite records.
- [x] React to Room updates in Compose.
- [x] Load a details screen from a navigation argument.
- [x] Model details with sealed UI state.
- [x] Render Favorites offline.
- [x] Remove favorites from UI.

## Future Improvements

- Enable Room schema export.
- Add DAO and migration tests.
- Normalize or convert genres more explicitly.
- Cache trending and details in Room.
- Add freshness timestamps and conflict/update policy.
- Add direct `DefaultMovieRepository` tests.
