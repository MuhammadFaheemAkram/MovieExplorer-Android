# Interview Notes

## Architecture

### Why use MVVM here?

MVVM separates Compose rendering from state and asynchronous work. Screens receive immutable state, while ViewModels own coroutine jobs, call use cases, and expose StateFlow. This improves testability and prevents business logic from accumulating in composables.

### Why call the architecture clean-ish rather than strict Clean Architecture?

The project has UI, domain, and data boundaries but keeps them in one app module. Some use cases are thin delegates, and feature packages own UI mappings. That is enough separation for the app's size without adding module and abstraction overhead.

### Why expose immutable StateFlow?

The ViewModel must be the only writer. Exposing `StateFlow` or `asStateFlow()` lets the UI observe the latest value without mutating it. It also makes state transitions easy to test.

### Why use `SharingStarted.WhileSubscribed(5_000)`?

It starts upstream work while the UI is collecting and keeps it alive for five seconds after the last subscriber. That avoids immediate restarts during short lifecycle changes while still allowing inactive flows to stop.

## Data And Domain

### Why use the Repository Pattern?

The repository gives ViewModels one movie API while hiding the fake service, in-memory trending cache, and Room favorites. Tests can replace it with an in-memory fake, and a real network implementation can be introduced behind the same domain contract.

### Why separate DTO, Entity, Domain, and UI models?

Each model answers a different need. DTOs match transport, entities match storage, domain models define application meaning, and UI models format display values. Separation reduces coupling, though it adds mapping code.

### What does `DefaultMovieRepository` coordinate?

It refreshes fake remote movies into an in-memory StateFlow, combines them with Room favorite IDs, maps Room entities to favorites, and resolves details from memory, Room, or fake API.

### Is this app offline-first?

Not fully. Favorites are offline-capable because Room stores full movie records. Trending and search are not persisted, and there is no freshness or synchronization policy.

### Why use a use-case layer when several use cases only delegate?

The layer names application actions and gives ViewModels stable dependencies. For a small app it is partly educational; in a larger app, validation, authorization, caching rules, or multi-repository coordination could live there.

## Room And DataStore

### Why use Room for favorites?

Favorites are structured records that need queries, observation, insert/replace, and delete behavior. Room validates SQL, generates implementations, and exposes Flow for reactive UI updates.

### Why store the complete movie instead of only its ID?

Storing complete data lets Favorites render offline and lets Details use a saved movie without another remote request. The tradeoff is duplicated data and a need for an update policy if a real backend changes movie metadata.

### Why use DataStore instead of SharedPreferences?

DataStore provides asynchronous, transactional updates and a Flow-based read API. It integrates naturally with coroutines and avoids synchronous preference reads on the UI thread.

### What exactly does the language setting do?

It persists and displays `English`, `Spanish`, or `Urdu`. It does not yet change Android resources or fake API content, so full localization remains roadmap work.

## Compose And Navigation

### Why split `HomeRoute` from `HomeScreen`?

`HomeRoute` handles Hilt ViewModel lookup and lifecycle-aware state collection. `HomeScreen` only renders state and invokes callbacks. This makes the screen easier to preview and test.

### How does navigation work?

Navigation Compose uses centralized string routes. Home/Search/Favorites are bottom destinations, Settings/About are drawer-only destinations, and Details uses `details/{movieId}` with an integer argument. Top-level navigation saves and restores state.

### How are one-time effects handled?

There is no dedicated effect stream. Navigation is emitted as UI callbacks handled by route composables, and the current app has no snackbar/toast events requiring Channel or SharedFlow. If such events grow, a typed effect stream could be added.

### Why use `collectAsStateWithLifecycle`?

It turns a Flow into Compose State while respecting the Android lifecycle, reducing unnecessary collection when the screen is not active.

## Coroutines And Search

### How is search debounced?

`SearchViewModel` maps its StateFlow to a trimmed query, waits 350 ms, removes duplicate values, and collects with `collectLatest`. A new query cancels the previous search block.

### How are errors represented?

Refresh uses a custom `MovieExplorerResult`; Search and Details use `runCatching` and write error messages into UI state. Exceptions do not reach composables directly.

### What would you improve about error handling?

Introduce typed domain errors, centralize throwable mapping, distinguish retryable/network/not-found failures, and use one consistent result strategy across refresh, search, and details.

## Dependency Injection And Scalability

### How does Hilt help?

Hilt constructs services, repositories, database objects, use cases, and ViewModels. Constructor dependencies are explicit, and interface bindings can be replaced without editing callers.

### How would you replace the fake API with a real backend?

Add Retrofit plus a serializer, implement `MovieApiService` with HTTP endpoints, map response DTOs to the existing domain model, provide authentication/configuration if needed, and change the Hilt binding from `FakeMovieApiService` to the real implementation.

### How would you scale this project?

Start by adding repository/DAO tests, API error types, Room trending cache, pagination, and CI. Split Gradle modules only when build time, ownership, or reuse justifies it, for example `core:model`, `core:database`, and feature modules.

### What would a full offline-first design require?

Room would become the observable source of truth for trending and details. Refresh would update Room in a transaction, entities would track freshness, failures would leave cached data visible, and WorkManager could optionally synchronize in the background.

## Testing

### How are ViewModels tested?

Tests replace `Dispatchers.Main`, use `runTest`, inject `FakeMovieRepository`, advance virtual time, and assert public StateFlow values.

### Why render stateless composables in UI tests?

It isolates visual behavior and callbacks from Hilt, Room, and navigation setup. Tests are smaller and failures identify a screen-state problem rather than an entire app-graph problem.

### What important tests are missing?

Room DAO/migration tests, direct movie repository tests, Favorites/Settings ViewModel tests, navigation tests, Details/Favorites UI tests, and screenshot regression tests.
