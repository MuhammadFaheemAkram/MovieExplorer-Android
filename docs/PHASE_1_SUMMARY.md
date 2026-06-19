# Phase 1 Summary: Foundation, Theme, And Navigation

## Phase Goal

Establish a compileable Android application shell with consistent package structure, Hilt setup, Material 3 theming, reusable design-system pieces, and complete navigation destinations before adding data behavior.

## What Was Implemented

- Kotlin/Compose Android app configuration.
- Hilt application and activity entry points.
- Light and dark Material 3 color schemes.
- Shared typography and spacing.
- Scaffold with top app bar, navigation drawer, and bottom navigation.
- Home, Search, Favorites, Settings, About, and Details routes.
- Integer `movieId` argument for Details.
- Back navigation for Details.
- Placeholder screen component and initial feature screen scaffolds.
- Bottom destinations limited to Home, Search, and Favorites.

The placeholder feature content was intentionally replaced by real feature implementations in later phases.

## Android Concepts Demonstrated

- `Application` setup with `@HiltAndroidApp`.
- `@AndroidEntryPoint` activity.
- Compose `setContent` and edge-to-edge layout.
- Material 3 `Scaffold` and theme composition.
- Navigation Compose route graph and arguments.
- Drawer and bottom-navigation state.
- Stateless reusable composables.
- Centralized routes and top-level destinations.

## Data Flow

Phase 1 had no movie data source yet:

```text
MainActivity
  -> MovieExplorerTheme
  -> MovieExplorerRoot
  -> MovieExplorerNavHost
  -> selected destination composable
```

Navigation events flowed from screen callbacks to the `NavHostController`.

## Important Files

- `MovieExplorerApp.kt`: Hilt-enabled application class.
- `MainActivity.kt`: Compose host and root theme entry point. Phase 4 later added settings observation here.
- `core/designsystem/theme/Theme.kt`: Light/dark Material 3 theme.
- `core/designsystem/theme/Color.kt`: Project color palette.
- `core/designsystem/theme/Type.kt`: Typography definitions.
- `core/designsystem/theme/Spacing.kt`: Shared spacing tokens.
- `core/navigation/MovieExplorerRoot.kt`: Drawer, app bars, bottom navigation, and root scaffold.
- `core/navigation/MovieExplorerNavHost.kt`: Destination graph.
- `core/navigation/MovieExplorerRoute.kt`: Route names and Details argument pattern.
- `core/navigation/TopLevelDestination.kt`: Icons and top-level destination lists.
- `core/designsystem/components/MovieExplorerPlaceholderScreen.kt`: Shared empty/placeholder presentation.

## Important Classes

### `MovieExplorerApp`

Starts Hilt's generated dependency graph at application scope.

### `MainActivity`

Hosts Compose. In the completed app it also observes app settings and applies dark mode, but its foundational responsibility remains hosting the UI tree.

### `MovieExplorerRoot`

Owns navigation chrome and determines whether to display a menu button, back button, or bottom navigation for the active route.

### `MovieExplorerRoute`

Keeps route names and titles in one sealed hierarchy and builds the Details route from a movie ID.

## Interview Notes

### Why build navigation before feature data?

It validates screen boundaries and app structure early. Later phases can add behavior inside stable destinations without redesigning the shell.

### Why is Details absent from the bottom bar?

Details is a contextual destination reached from a selected movie, not a top-level section. It uses back navigation instead.

### Why keep route constants centralized?

It reduces string duplication and keeps route titles/arguments discoverable. A larger app could move to Navigation Compose's serializable typed routes.

### Why separate route composables and screen composables?

Route composables own Android/navigation/ViewModel integration. Screens remain state-and-callback functions that are easier to test.

## Learning Checklist

- [x] Configure a Compose application.
- [x] Create an `Application` class for Hilt.
- [x] Mark an activity as a Hilt entry point.
- [x] Build a Material 3 theme.
- [x] Create shared spacing and typography tokens.
- [x] Build a Navigation Compose graph.
- [x] Pass a typed integer navigation argument.
- [x] Combine drawer and bottom navigation.
- [x] Hide bottom navigation on contextual screens.
- [x] Keep reusable composables stateless.

## Future Improvements

- Replace string routes with serializable typed Navigation Compose routes.
- Add navigation-focused instrumentation tests.
- Add previews for reusable design-system states.
- Improve adaptive navigation for larger screens.
