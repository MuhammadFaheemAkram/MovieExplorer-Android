# Roadmap

This roadmap extends the existing app without pretending missing work is already complete.

## Priority 1: Repository Presentation

- Add real screenshots for Home, Search, Details, Favorites, Settings, and dark mode.
- Choose and add an open-source `LICENSE` file.
- Add repository topics, a concise GitHub description, and release notes.
- Add a CI workflow for debug build, unit tests, lint, and androidTest APK compilation.

## Priority 2: Testing Confidence

- Add in-memory Room DAO tests.
- Enable Room schema export and add migration tests before database version 2.
- Add direct `DefaultMovieRepository` tests for source precedence and favorite merging.
- Add Favorites, Settings, and Main ViewModel tests.
- Add Details and Favorites Compose UI tests.
- Add navigation tests for bottom navigation, drawer destinations, and details back behavior.
- Evaluate Paparazzi or Roborazzi for snapshot regression tests.

## Priority 3: Real Data

- Add Retrofit and Kotlin Serialization or another supported converter.
- Implement a real `MovieApiService` behind the existing interface.
- Move API keys and base URLs into safe build configuration.
- Add typed network/domain errors and retry rules.
- Replace demo Picsum images with backend poster/backdrop paths.

## Priority 4: Better Offline Behavior

- Cache trending and details in Room.
- Make Room the source of truth for movie lists.
- Add cache timestamps and a freshness policy.
- Keep cached content visible when refresh fails.
- Consider WorkManager only if background synchronization becomes a real requirement.

## Priority 5: Larger Data Sets

- Add Paging 3 for trending and search.
- Preserve scroll position and query state across navigation.
- Add sort/filter controls if supported by the data source.

## Priority 6: Settings And Localization

- Apply the stored language preference to Android string resources.
- Translate user-facing strings.
- Decide whether content language affects only UI text or also backend movie data.
- Add a system/default theme option alongside the Boolean dark-mode setting.

## Priority 7: Accessibility And UI Polish

- Audit TalkBack labels and traversal order.
- Check touch target sizes and dynamic font scaling.
- Verify color contrast in light and dark themes.
- Add deterministic image placeholders and failure visuals.
- Add adaptive layouts for tablets and landscape.

## Priority 8: Modularization When Justified

The current single app module is appropriate for the project size. If features and build times grow, consider:

- `core:model`
- `core:network`
- `core:database`
- `core:designsystem`
- Feature modules for Home, Search, Favorites, Details, and Settings

Modularization should solve a measurable build, ownership, or reuse problem rather than serve as decoration.
