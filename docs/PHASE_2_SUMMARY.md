# Phase 2 Summary: Fake API, Repository, And Home

## Phase Goal

Build the first complete vertical feature slice: fake remote movie data flows through DTO/domain mappings, repository and use cases, a StateFlow ViewModel, and a polished Home screen.

## What Was Implemented

- Domain `Movie` model.
- Remote `MovieDto` model.
- `MovieApiService` contract.
- `FakeMovieApiService` with eight hard-coded movies.
- Simulated delays and optional error mode.
- Movie mapping between remote and domain layers.
- `MovieRepository` contract and default implementation.
- In-memory trending movie cache.
- Trending observation and refresh use cases.
- Favorite toggle use-case boundary.
- `MovieExplorerResult` for refresh success/error.
- `HomeViewModel` with immutable `StateFlow<HomeUiState>`.
- Loading, refreshing, empty, error, and success Home states.
- Adaptive movie grid, Coil images, ratings, years, genres, and favorite action.
- Pull to refresh.

Room-backed favorite persistence was added in Phase 3. Phase 2 established the repository and UI boundaries that made that change possible.

## Android Concepts Demonstrated

- Retrofit-style interface without requiring a backend.
- Coroutine `delay` for simulated latency.
- DTO/domain separation.
- Repository Pattern.
- Use-case classes.
- StateFlow combination and `stateIn`.
- `viewModelScope`.
- Result/error mapping.
- Lifecycle-aware Flow collection.
- Lazy adaptive grids.
- Coil image loading.
- Material 3 pull to refresh.

## Data Flow

```text
HomeScreen refresh/retry
  -> HomeViewModel
  -> RefreshTrendingMoviesUseCase
  -> MovieRepository
  -> FakeMovieApiService
  -> List<MovieDto>
  -> DTO/domain mapper
  -> repository trending StateFlow
  -> GetTrendingMoviesUseCase
  -> HomeViewModel combines data + loading state
  -> HomeUiState
  -> HomeScreen
```

## Important Files

- `data/model/MovieDto.kt`: Fake remote representation.
- `domain/model/Movie.kt`: Domain representation.
- `data/remote/MovieApiService.kt`: Remote-service contract.
- `data/remote/FakeMovieApiService.kt`: Hard-coded data, delay, search, and details behavior.
- `core/network/NetworkConfig.kt`: Error simulation flag.
- `data/mapper/MovieMappers.kt`: Remote/domain conversion, later expanded for Room.
- `domain/repository/MovieRepository.kt`: Movie data contract.
- `data/repository/DefaultMovieRepository.kt`: Coordinates remote, memory, and, after Phase 3, Room.
- `domain/usecase/GetTrendingMoviesUseCase.kt`: Exposes trending Flow.
- `domain/usecase/RefreshTrendingMoviesUseCase.kt`: Refreshes and maps failures to `MovieExplorerResult`.
- `feature/home/HomeViewModel.kt`: Owns Home state transitions.
- `feature/home/HomeUiState.kt`: Immutable Home state.
- `feature/home/HomeScreen.kt`: Stateless Home UI.
- `feature/home/MovieUiModel.kt`: Presentation-ready Home movie.

## Important Classes

### `FakeMovieApiService`

Implements the same suspend contract a real Retrofit service could implement. It includes deterministic demo records, title/genre search, details lookup, delays, and optional errors.

### `DefaultMovieRepository`

Owns the in-memory trending source and hides data-source coordination. Its current implementation also merges Room favorites added later.

### `HomeViewModel`

Combines repository movie data with internal loading/refresh/error state. UI events call public functions instead of mutating state directly.

### `HomeUiState`

Represents all values required to render Home in one immutable object.

## Interview Notes

### Why fake an API behind an interface rather than returning a list from the ViewModel?

The interface preserves a realistic data boundary. Replacing the fake with Retrofit does not require rewriting the ViewModel or use cases.

### Why keep an in-memory StateFlow in the repository?

Refresh is a suspend command, while Home needs an observable stream. Updating a repository StateFlow separates the command from ongoing observation.

### Why use a separate UI model?

The UI model formats rating as a string and gives the screen a presentation-specific contract without changing the domain model.

### Why distinguish loading and refreshing?

Initial loading can replace the screen with a progress indicator. Refreshing should keep existing content visible while showing pull-to-refresh progress.

## Learning Checklist

- [x] Define a suspend API interface.
- [x] Implement a deterministic fake service.
- [x] Simulate latency and errors.
- [x] Separate DTO and domain models.
- [x] Add mapper functions.
- [x] Define repository and use-case boundaries.
- [x] Expose immutable Home StateFlow.
- [x] Combine repository data and UI operation state.
- [x] Render loading/error/empty/success states.
- [x] Add pull to refresh and image loading.

## Future Improvements

- Replace the fake service with Retrofit.
- Add typed network/domain errors.
- Add repository tests for refresh and favorite merging.
- Persist trending data in Room rather than only memory.
- Add pagination for larger result sets.
