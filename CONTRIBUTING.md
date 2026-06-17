# Contributing

Thanks for taking a look at Movie Explorer. This project is intentionally small, but it tries to model habits that scale to larger Android apps.

## Local Setup

1. Open the project in Android Studio.
2. Let Gradle sync.
3. Build the debug app:

```bash
./gradlew :app:assembleDebug
```

4. Run local tests:

```bash
./gradlew :app:testDebugUnitTest
```

5. Compile instrumentation tests:

```bash
./gradlew :app:assembleDebugAndroidTest
```

## Contribution Guidelines

- Keep composables mostly stateless.
- Put business logic in ViewModels, use cases, or repositories.
- Prefer Flow/StateFlow for observable state.
- Add mappers when data crosses remote, database, domain, or UI boundaries.
- Add tests for new ViewModel logic and important UI states.
- Keep features small and readable; this is a learning app, not a showcase for abstraction.

## Good First Issues

- Add UI tests for Details and Favorites.
- Add Room migration tests after a database schema change.
- Add system-theme mode in addition to the current dark-mode toggle.
- Add localization resources for supported content languages.
- Add screenshot testing with a dedicated library such as Paparazzi or Roborazzi.
