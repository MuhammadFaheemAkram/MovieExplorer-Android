# Testing Guide

Movie Explorer uses two kinds of tests today: local JVM tests and Compose UI instrumentation tests.

## Local Unit Tests

Run:

```bash
./gradlew :app:testDebugUnitTest
```

These tests cover:

- Use cases
- ViewModel state transitions
- DataStore-backed settings behavior

The test fake repository lives in:

```text
app/src/test/java/com/example/movieexplorer/testing/FakeMovieRepository.kt
```

The main dispatcher rule lives in:

```text
app/src/test/java/com/example/movieexplorer/testing/MainDispatcherRule.kt
```

## Compose UI Tests

Compile the UI test APK:

```bash
./gradlew :app:assembleDebugAndroidTest
```

Run on a connected emulator or device:

```bash
./gradlew :app:connectedDebugAndroidTest
```

Current Compose UI tests render stateless screens directly:

- Home loading/success/error states
- Search empty/result states
- Settings dark-mode and language interactions

Stable test tags live in:

```text
app/src/main/java/com/example/movieexplorer/core/designsystem/MovieExplorerTestTags.kt
```

## Snapshot Testing

Snapshot testing is not configured yet. For an open-source Android app, a good next step would be adding Paparazzi or Roborazzi for JVM-rendered screenshots of stateless Compose screens.

Suggested snapshot candidates:

- Home success grid in light and dark theme
- Details success screen
- Empty Favorites state
- Search no-results state
- Settings screen

Keeping snapshot targets stateless makes failures easier to understand and avoids brittle dependency injection setup.
