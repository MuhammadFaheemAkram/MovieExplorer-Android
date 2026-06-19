# Learning Notes

## MVVM

Movie Explorer separates rendering from state ownership:

- Compose screens render immutable UI state.
- Route composables obtain ViewModels and collect flows.
- ViewModels process UI events and call use cases.
- Repositories coordinate data sources.

This keeps Android UI code small and makes ViewModels testable without rendering Compose.

## Jetpack Compose

The app uses Material 3 composables, `Scaffold`, top app bars, a modal drawer, bottom navigation, lazy grids/lists, cards, chips, text fields, switches, dropdown menus, and pull to refresh.

Screens are mostly stateless. For example, `HomeScreen` receives `HomeUiState` plus callbacks; `HomeRoute` owns the ViewModel connection. This pattern improves previewability and UI testing.

`collectAsStateWithLifecycle` stops unnecessary collection when the UI lifecycle is not active.

## StateFlow And Immutable State

ViewModels expose `StateFlow<UiState>`. Mutable flows are private.

Benefits:

- The UI has one current state value.
- State survives recomposition.
- Tests can inspect state deterministically.
- Callers cannot modify ViewModel state directly.

`stateIn` converts cold repository flows into hot state streams. Several ViewModels use `SharingStarted.WhileSubscribed(5_000)` to keep upstream work alive briefly across short UI interruptions.

## Coroutines And Flow

Suspend functions represent one-time work such as refresh, search, details loading, favorite updates, and settings updates.

`viewModelScope` ties coroutine lifetime to the ViewModel.

Search demonstrates Flow operators:

```text
query StateFlow
  -> trim
  -> debounce(350)
  -> distinctUntilChanged
  -> collectLatest
  -> search use case
```

`collectLatest` cancels the previous search block when a newer query arrives.

## Repository Pattern

`MovieRepository` hides fake API, in-memory cache, and Room details from ViewModels. `SettingsRepository` hides DataStore.

The repository boundary lets tests substitute `FakeMovieRepository`. It also makes a real API implementation possible without changing feature ViewModels.

The tradeoff is that `DefaultMovieRepository` currently coordinates several concerns. If the app grows, separate remote/local data sources and clearer cache policies would help.

## Use Cases

Use cases give application actions explicit names:

- Refresh trending movies.
- Search movies.
- Get details.
- Observe and toggle favorites.
- Observe and update settings.

Most are thin delegates, which is appropriate for this learning app. Use cases become more valuable when business rules grow or several ViewModels share the same operation.

## Hilt

Hilt creates the object graph from `MovieExplorerApp`.

- `DataModule` binds interfaces to implementations.
- `DatabaseModule` provides Room and `MovieDao`.
- `DataStoreModule` provides the Preferences DataStore.
- ViewModels use constructor injection with `@HiltViewModel`.

Dependency injection removes manual factories and makes dependencies visible in constructors.

## Model Separation

The app uses separate model types:

- DTO for fake network data.
- Entity for the Room table.
- Domain model for repository/use-case contracts.
- UI models for formatted presentation data.

This prevents storage and presentation concerns from leaking across layers. It adds mapping code, so it should be used where boundaries are meaningful rather than mechanically everywhere.

## Room

Room stores complete favorite movie records. `MovieDao` exposes favorite lists and IDs as Flow, plus suspend read/write operations.

Because complete records are stored, the Favorites screen can render offline. Trending is not persisted, so the app is only partially offline-capable.

The current schema stores genres as a pipe-delimited string. A TypeConverter or normalized genre table could be considered for more complex queries.

## DataStore

Preferences DataStore stores dark mode and a language code. It replaces direct SharedPreferences access with a coroutine/Flow API.

DataStore is accessed through `SettingsRepository`, never directly from a composable. `MainViewModel` observes settings so dark mode can update the root theme immediately.

The language setting is persistence-only today; it does not localize Android resources.

## Navigation Compose

Routes are centralized in `MovieExplorerRoute`. The app uses:

- Bottom navigation for Home, Search, and Favorites.
- A drawer for Home, Search, Favorites, Settings, and About.
- An integer `movieId` argument for Details.
- Saved/restored top-level navigation state.
- A details back button.

Route composables translate navigation events into `NavController` calls, leaving screen composables navigation-agnostic.

## Loading, Error, Empty, And Success States

The UI does not assume all data requests succeed.

- Home has initial loading and refresh-specific state.
- Search has empty query, loading, error, no results, and results.
- Details has a sealed loading/success/error state.
- Favorites has empty and content states.

This makes failure handling part of feature design rather than an afterthought.

## Testing

Unit tests use repository fakes and coroutine test dispatchers. Compose UI tests render stateless screens and verify visible states/callbacks.

Important lessons:

- Test behavior and state transitions rather than private functions.
- Control coroutine time when testing debounce.
- Keep screen inputs explicit so UI tests do not require the production object graph.
- Compile and run instrumentation tests separately from JVM tests.

## Offline Capability

Favorites are available offline because Room stores full records. Details can resolve a saved favorite from Room when it is not in the in-memory trending cache.

This is not a complete offline-first design because trending/search data is not cached in Room, there is no freshness policy, and there is no background synchronization.
