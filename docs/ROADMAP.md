# Roadmap

This roadmap extends the existing app without pretending missing work is already complete.

## Priority 1: Repository Presentation

- Add real screenshots for Home, Search, Details, Favorites, Settings, and dark mode.
- Choose and add an open-source `LICENSE` file.
- Add repository topics, a concise GitHub description, and release notes.
- Add a CI workflow for debug build, unit tests, lint, and androidTest APK compilation.

## Priority 2: Testing Confidence

- When Room returns as an offline cache: add DAO tests, enable schema export, and add migration tests.
- Add MockWebServer tests for `MovieApiService` JSON parsing (`DefaultMovieRepository` tests were added in Phase 5).
- Add Favorites, Settings, and Main ViewModel tests.
- Add Details and Favorites Compose UI tests.
- Add navigation tests for bottom navigation, drawer destinations, and details back behavior.
- Evaluate Paparazzi or Roborazzi for snapshot regression tests.

## Priority 3: Real Data

Done in Phase 5: Retrofit + kotlinx.serialization, a real `MovieApiService` for the FastAPI backend, a `BuildConfig` base URL, and `MovieExplorerApiException` error mapping. Remaining:

- Protect write endpoints on the backend (API key or JWT with an admin role). Phase 7's Manage movies switch only hides UI; the API still accepts writes from anyone.
- Add retry rules (for example exponential backoff for idempotent requests).
- Serve real poster/backdrop images from the backend instead of Picsum seeds.

## Priority 4: Better Offline Behavior

- Reintroduce Room as an offline cache for trending, details, and favorites (removed in Phase 5).
- Make Room the source of truth for movie lists, synced from the backend.
- Add cache timestamps and a freshness policy.
- Keep cached content visible when refresh fails.
- Consider WorkManager only if background synchronization becomes a real requirement.

## Priority 5: Larger Data Sets

- Add Paging 3 for trending and search (the backend already supports `limit`/`offset` and returns `total`).
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
