# Testing Guide

## Current Setup

Movie Explorer has local JVM tests in `app/src/test` and Compose UI instrumentation tests in `app/src/androidTest`.

The test dependencies are JUnit 4, kotlinx-coroutines-test, AndroidX Test, Espresso, and Compose UI Test. Hilt and Room code is compiled for test variants, but there is no custom Hilt test application or in-memory Room instrumentation suite.

## Run The Tests

Run all local JVM tests:

```bash
./gradlew :app:testDebugUnitTest
```

Compile the instrumentation test APK without a device:

```bash
./gradlew :app:assembleDebugAndroidTest
```

Run Compose UI tests on a connected device or emulator:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Build the application as a separate sanity check:

```bash
./gradlew :app:assembleDebug
```

## Unit And Use Case Tests

`SearchMoviesUseCaseTest` verifies that the query reaches the repository and the result is returned.

`ToggleFavoriteMovieUseCaseTest` verifies add/remove behavior through `FakeMovieRepository`.

These are intentionally narrow tests. The use cases mostly delegate, so their main value is documenting the domain boundary and protecting method wiring.

## ViewModel Tests

`HomeViewModelTest` covers:

- Successful initial refresh.
- Failed initial refresh.
- Loading completion, movie mapping, and error messages.

`SearchViewModelTest` covers:

- The 350 ms debounce.
- Cancellation of an earlier partial query by a later query.
- Successful result state.
- Search error state.

`DetailsViewModelTest` covers:

- Loading a movie into success state.
- Toggling favorite state.
- Repository favorite updates.

`MainDispatcherRule` replaces `Dispatchers.Main` with a test dispatcher. `runTest`, `advanceTimeBy`, and `advanceUntilIdle` make coroutine timing deterministic.

`FakeMovieRepository` implements the production `MovieRepository` contract with in-memory StateFlows and configurable `Result` values.

There are currently no direct tests for `FavoritesViewModel`, `SettingsViewModel`, or `MainViewModel`.

## Repository Tests

`DefaultSettingsRepositoryTest` uses a real temporary Preferences DataStore. It verifies default values and persisted dark-mode/language updates.

There is no direct `DefaultMovieRepository` test. Its coordination of fake API data, in-memory trending state, and Room favorite state is currently covered only indirectly through use-case/ViewModel tests and app behavior.

## Room Tests

Room runtime and compiler dependencies are present, but there are no DAO, database, or migration tests.

Important missing cases:

- Insert and read a favorite.
- Replace an existing favorite.
- Delete a favorite.
- Observe favorite IDs.
- Verify title ordering.
- Verify future schema migrations.

An in-memory Room database in `androidTest` would be a practical next step. Schema export must be enabled before meaningful migration testing is added.

## Compose UI Tests

The UI tests render stateless screens directly with `createComposeRule`, avoiding Hilt setup and real data sources.

`HomeScreenTest` covers:

- Loading indicator.
- Movie content.
- Card click.
- Favorite click.
- Error and retry action.

`SearchScreenTest` covers:

- Empty-query prompt.
- Text input callback.
- Result rendering.
- Result click.

`SettingsScreenTest` covers:

- Dark-mode switch callback.
- Language dropdown callback.

Stable semantics tags are defined in `MovieExplorerTestTags`. This makes tests less dependent on display text while still allowing text assertions for visible behavior.

The UI test APK is compiled by `assembleDebugAndroidTest`; compiling it does not execute tests. Execution requires `connectedDebugAndroidTest` and a running emulator or device.

## Snapshot Testing

Snapshot testing is not configured. Paparazzi or Roborazzi could add deterministic image regression tests for stateless Compose screens.

Good snapshot candidates:

- Home success state in light and dark themes.
- Home error and empty states.
- Details success state.
- Empty Favorites.
- Search no-results state.
- Settings.

Snapshot tests should complement behavior assertions, not replace them.

## Tests To Add Next

1. DAO tests with an in-memory Room database.
2. `DefaultMovieRepository` tests for source precedence and favorite merging.
3. `FavoritesViewModel` and `SettingsViewModel` tests.
4. Navigation tests for bottom bar, drawer, and details back behavior.
5. Details/Favorites Compose UI tests.
6. Room migration tests after schema export is enabled.
7. Snapshot tests for stable screen states.
