# Phase 7 Summary: Edit, Delete, And The Manage Movies Switch

## Goal

Use the backend's remaining write endpoints (`PATCH` and `DELETE /api/v1/movies/{id}`) from the app, and make every write action (add, edit, delete) available only when a user turns it on.

## User Flow

```text
Settings > Manage movies (off by default, saved in DataStore)
  off: read-only app. No "Add movie" button, no Edit/Delete on Details.
  on:  Home shows "Add movie"; Details shows Edit and Delete.

Details > Edit
  -> form pre-filled from GET /movies/{id}  ("Save changes")
  -> PATCH /movies/{id}
  -> fresh Details replaces the old one (Back returns to the previous list)

Details > Delete
  -> confirmation dialog ("removed for everyone, including from favorites")
  -> DELETE /movies/{id} (204)
  -> navigate back; Home, Favorites and Search update without a manual refresh
```

## Important: The Switch Is Not Security

"Manage movies" only **hides buttons on this device**. The backend has no authentication yet, so anyone who can reach the API (curl, Postman, a modified app) can still create, edit, or delete movies.

Real protection belongs on the server:

1. **Short term:** require an admin API key header (for example `X-Admin-Key`) on `POST`, `PATCH`, and `DELETE`. FastAPI can enforce it with one dependency.
2. **Proper:** user accounts with JWT login and a role (`admin` / `viewer`). The app then shows the write actions based on the logged-in user's role instead of a local switch, and the backend rejects writes from non-admins with 403.

## What Changed

| Layer | Change |
| --- | --- |
| Settings | `UserSettings.movieManagementEnabled`, DataStore key `movie_management_enabled`, `UpdateMovieManagementUseCase`, a switch on the Settings screen |
| Gating | `MainActivity` passes the setting to `MovieExplorerRoot`, which hides the Home button and passes `canManageMovies` to Details through the NavHost |
| Domain | `NewMovie` renamed to `MovieDraft` (now used for create and update); `UpdateMovieUseCase`, `DeleteMovieUseCase`, `ObserveCatalogChangesUseCase` |
| Data | `MovieRequestDto` (renamed from `CreateMovieRequestDto`); `updateMovie` / `deleteMovie` in Retrofit and the repository, which also update the in-memory Home and Favorites caches |
| Form | `feature/addmovie` renamed to `feature/movieform`. One `MovieFormViewModel` handles both modes; edit mode loads and pre-fills the movie and shows loading / load-error states |
| Details | Edit and Delete buttons, confirmation dialog, `isDeleting` / `actionErrorMessage` in `Success`, new `Deleted` state that triggers navigation back |
| Navigation | New route `edit_movie/{movieId}` (`MovieExplorerRoute.EditMovie`) |
| Search | Re-runs the current query when the repository's catalog version changes |

## Bug Found During The Emulator Test

After deleting a movie, Search still listed it, even in its pre-edit form. The Search screen keeps its own copy of the results and only searched again when the query text changed. The repository now exposes `observeCatalogVersion()`, a number that goes up after every create, update, or delete, and `SearchViewModel` combines it with the debounced query. A regression test (`results reload after a movie is deleted elsewhere`) covers it.

## Where Favorites Live

Favorites are stored in the backend's `favorites` table (PostgreSQL), not in Room. Room was removed in Phase 5. See the Phase 5 summary and `DefaultMovieRepository` for the flow.

## Tests

- JVM: 36 tests pass (new: edit-mode form loading and saving, load error, delete success and failure, repository update and delete, settings persistence, Search reload).
- Instrumented: 15 tests pass (new: `DetailsScreenTest` for hidden buttons, edit callback, and delete confirmation; Settings switch; "Save changes" label).
- Manual run on the `Pixel_10_Pro` emulator against the backend: switch off hides actions, switch on shows them, edit (PATCH 200), delete with confirmation (DELETE 204, favorite removed too), and Search reload after delete.
