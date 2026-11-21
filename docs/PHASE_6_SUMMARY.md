# Phase 6 Summary: Add Movie

## Goal

Let users create movies from the app using the backend's `POST /api/v1/movies` endpoint, so the app performs a real write operation instead of only reading.

## User Flow

```text
Home "Add movie" button
  -> Add Movie form (back arrow, no bottom bar)
  -> Save
     -> invalid input: field errors under each field, nothing is sent
     -> 409 duplicate / no connection: error card above Save, form kept
     -> 201 Created: navigate to the new movie's Details (form removed from back stack)
  -> Back: Home, where the movie is already listed in rating order
```

## New And Changed Files

| Layer | File | Purpose |
| --- | --- | --- |
| Domain | `domain/model/NewMovie.kt` | A movie to create (no id, no favorite state) |
| Domain | `domain/usecase/CreateMovieUseCase.kt` | Returns `MovieExplorerResult<Movie>` |
| Domain | `MovieRepository.createMovie()` | New contract method |
| Data | `CreateMovieRequestDto`, `MovieApiService.createMovie()` | `POST movies` body and call |
| Data | `DefaultMovieRepository.createMovie()` | Posts, then inserts the result into the trending cache in backend order |
| Data | `ApiCall.kt` | 422 errors now read as `"rating: Input should be ..."` instead of a generic message |
| Feature | `feature/addmovie/AddMovieFormValidator.kt` | Pure Kotlin rules mirroring the backend schema |
| Feature | `AddMovieUiState.kt`, `AddMovieViewModel.kt`, `AddMovieScreen.kt` | State, logic, and UI |
| Navigation | `MovieExplorerRoute.AddMovie`, NavHost, Root | Route, back arrow, Home floating action button |

## Validation Rules (same as the backend)

| Field | Rule |
| --- | --- |
| Title | Required, at most 200 characters |
| Overview | Required, at most 5000 characters |
| Rating | Number from 0 to 10 (`7,5` is accepted as `7.5`) |
| Release year | Whole number from 1888 to 2100 |
| Genres | Comma separated, trimmed, case-insensitive duplicates removed, at most 10, each at most 50 characters |
| Poster / backdrop URL | Optional; must start with `http://` or `https://`. Empty fields get a deterministic Picsum placeholder seeded from the title |

The backend validates everything again. Client validation is for fast feedback, not security.

## Lessons Learned

### TextField state should be snapshot state

The first version kept the form in a `StateFlow`, like the other screens. A `StateFlow` is collected into Compose one frame later, so a `TextField` can receive a stale value while the keyboard is still sending characters, and characters get dropped. `AddMovieViewModel` now exposes `var uiState by mutableStateOf(...)` instead, following Google's guidance for text input state. The Search screen still uses a `StateFlow` for its short query; switching it is optional.

### Fix bugs on both sides of the API

Matching the client rules to the backend revealed a backend bug: `genres.name` is `VARCHAR(50)` but the schema did not check the length, so a long genre crashed the request with HTTP 500. The FastAPI schema now rejects it with 422, and a regression test covers it.

### Old Espresso breaks on new Android versions

`connectedDebugAndroidTest` failed for every UI test (including the existing ones) with `NoSuchMethodException: InputManager.getInstance`. Espresso 3.5.1 uses a hidden Android API that newer Android versions removed. Updating to `espresso-core` 3.7.0 and `androidx.test.ext:junit` 1.3.0 fixed it.

## Tests

- `AddMovieFormValidatorTest` (5): parsing, placeholders, required fields, ranges, genre limits.
- `AddMovieViewModelTest` (4): errors block the request, editing clears one error, success exposes the id, server error keeps the form.
- `DefaultMovieRepositoryTest` (+2): create inserts in rating order; 422 message mapping.
- `AddMovieScreenTest` (3, instrumented): typing and save callbacks, error display, disabled button while saving.
- Totals: 28 JVM tests and 10 instrumented tests pass.
- Manual run on the `Pixel_10_Pro` emulator against the backend: empty-form validation (no request sent), create (201) → Details → Home ordering, duplicate (409) message.
