# Pokédex Clone

An Android application built with modern Android development practices, designed to simulate a Pokédex. Users can browse a list of Pokémon, view their details, sort and filter the list, and manage their favorite Pokémon. The application fetches data from the public PokeAPI and uses Jetpack Compose for the UI.

## Key Features

*   **Browse Pokémon List**: Displays a scrollable list of Pokémon with their names and sprites.
*   **Pokémon Details**: Tapping on a Pokémon navigates to a detail screen (implementation of detail screen content, like abilities, is separate).
*   **Sorting**: The Pokémon list can be sorted by:
    *   Pokédex ID (numerical order - default)
    *   Name (alphabetically, A-Z)
    *   Name (reverse alphabetically, Z-A)
*   **Filtering**: The Pokémon list can be filtered to show:
    *   All Pokémon
    *   Only Favorite Pokémon
*   **Favorites System**: Pokémon can be marked/unmarked as favorites (functionality within `Pokemon` model and `PokemonRepository`).
*   **Loading & Error States**:
    *   Displays a loading indicator (`CircularProgressIndicator`) while data is being fetched.
    *   Shows user-friendly error messages if data fetching fails, with a "Retry" option.
*   **Efficient Data Handling**: Implements an in-memory cache (`pokemonCacheFlow` in `PokemonRepository`) for Pokémon and ability data to improve performance and user experience on subsequent loads.
*   **Image Loading**: Uses [Glide](https://github.com/bumptech/glide) for efficiently loading and displaying Pokémon sprites.

## Tech Stack & Architecture

*   **Programming Language**: [Kotlin](https://kotlinlang.org/) ( leveraging coroutines and Flow)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) for building the UI declaratively.
    *   Uses Material 3 components (`androidx.compose.material3`).
*   **Architecture**: Model-View-ViewModel (MVVM)
    *   **ViewModel**: [Jetpack ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) (`PokemonListViewModel`, potentially `PokemonDetailViewModel`) to manage UI-related data lifecycle-consciously.
    *   **Repository Pattern**: `PokemonRepository` (implementing `PokemonRepositoryInterface`) abstracts data sources (network and in-memory cache) and provides a clean API for data access to ViewModels.
*   **Dependency Injection**: [Hilt](https://dagger.dev/hilt/) for managing dependencies throughout the application.
    *   `@HiltAndroidApp` used on the `PokedexApp` class.
    *   `@AndroidEntryPoint` on `MainActivity`.
    *   `@HiltViewModel` on ViewModels.
    *   Modules like `AppModule` provide dependencies (e.g., Retrofit, OkHttpClient, `PokemonRepositoryInterface`).
*   **Networking**:
    *   [Retrofit 2](https://square.github.io/retrofit/) (`libs.retrofit`) for declarative, type-safe HTTP calls to the PokeAPI.
    *   [OkHttp 3](https://square.github.io/okhttp/) as the underlying HTTP client (implicitly via Retrofit).
    *   [Gson](https://github.com/google/gson) (`libs.retrofit.converter`) for parsing JSON data from the API.
*   **Asynchronous Programming**:
    *   [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) (`libs.kotlinx.coroutines.core`, `libs.kotlinx.coroutines.android`) for managing background tasks and asynchronous operations.
    *   [Kotlin Flow](https://kotlinlang.org/docs/flow.html) for reactive data streams from the repository to the ViewModel (`pokemonCacheFlow`, `abilityCacheFlow`) and from ViewModel to UI (`uiState`, `pokemonListStateFlow`).
*   **Navigation**: [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation) (`libs.androidx.navigation.compose`) for navigating between `PokemonListScreen` and `PokemonDetailScreen`.
*   **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`).
    *   Uses Version Catalogs (`libs.` prefix for dependencies).
*   **Image Loading**: [Glide Compose Integration](https://bumptech.github.io/glide/doc/compose.html) (`libs.glide.compose`) for displaying Pokémon sprites.

## API Used

*   **[PokeAPI (v2)](https://pokeapi.co/)**: A free and open RESTful API for Pokémon data.
    *   Endpoints used:
        *   `pokemon?limit=1302` (for the list)
        *   `pokemon/{id}` (for Pokémon details)
        *   `ability/{id}` (for ability details)

## Project Structure Highlights

*   **`PokedexApp.kt`**: The `Application` class, annotated with `@HiltAndroidApp` to initialize Hilt.
*   **`MainActivity.kt`**: The main entry point `Activity`, annotated with `@AndroidEntryPoint`. It sets up the Compose UI using `AppTheme` and `MainScreen`.
*   **`ui/MainScreen.kt`**: Composable function that sets up the `NavHost` for Jetpack Navigation Compose, defining routes to `PokemonListScreen` and `PokemonDetailScreen`.
*   **`di/AppModule.kt`**: Hilt module (`@InstallIn(SingletonComponent::class)`) providing application-wide singleton dependencies:
    *   `OkHttpClient`
    *   `Retrofit` (configured with `BASE_URL = "https://pokeapi.co/api/v2/"`)
    *   `PokemonApi` (Retrofit service interface)
    *   `PokemonRepositoryInterface` (bound to `PokemonRepository`)
*   **Data Layer (`data/`)**:
    *   **`model/Pokemon.kt`**: Data class for Pokémon (id, name, sprite, isFavorite, abilityIds).
    *   **`model/Ability.kt`**: Data class for Pokémon abilities (id, name, description).
    *   **`remote/PokemonApi.kt`**: Retrofit interface defining API GET endpoints.
    *   **`repository/PokemonRepositoryInterface.kt`**: Interface defining the contract for Pokémon data operations.
    *   **`repository/PokemonRepository.kt`**: Concrete implementation of `PokemonRepositoryInterface`. Handles fetching data from `PokemonApi`, parsing, and caching in `StateFlow`s (`_pokemonCacheFlow`, `_abilityCacheFlow`). Implements `extractIdFromUrl` utility.
*   **UI Layer (`ui/`)**:
    *   **`pokemonlist/`**: Feature package for the Pokémon list.
        *   **`PokemonListViewModel.kt`**: Manages the state for `PokemonListScreen`. Exposes `uiState` (Loading, Error, ShowList), `pokemonListStateFlow`, `sortType`, and `filter`. Contains logic for loading data, sorting, and filtering.
        *   **`PokemonListScreen.kt`**: Composable screen displaying the top app bar, filter chips, and a `LazyColumn` of Pokémon. Handles different UI states from the ViewModel. Navigates to detail screen on item click. Includes `LaunchedEffect` to scroll to top on sort/filter changes.
        *   **`PokemonListItem.kt`**: Composable function for displaying a single Pokémon item in the list, using `GlideImage` for the sprite.
        *   **`SortType.kt`**, **`PokemonFilter.kt`**: Enums/sealed classes defining sorting and filtering options.
    *   **`pokemondetail/PokemonDetailScreen.kt`**: Composable screen for Pokémon details (current content might be a placeholder, but navigation to it is set up).
    *   **`theme/`**: Contains `AppTheme.kt`, `Color.kt`, `Type.kt` for Jetpack Compose theming.

## Setup and Installation

1.  **Prerequisites**:
    *   Android Studio (latest stable version, Giraffe or newer recommended for full compatibility with Android Gradle Plugin and Compose versions).
    *   Java Development Kit (JDK) 11 or higher (as specified in `compileOptions`).
2.  **Clone the repository**:
    ```bash
    git clone <your-repository-url>
    cd pokedex-clone
    ```
3.  **Open in Android Studio**:
    *   Select "Open" or "Import Project".
    *   Navigate to the cloned `pokedex-clone` directory and select it.
4.  **Sync Gradle**:
    *   Allow Android Studio to sync the project with Gradle files. This will download all specified dependencies (from `libs.versions.toml` via `app/build.gradle.kts`).
5.  **Build the Project**:
    *   Once synced, build the project: `Build > Make Project` (or `Cmd+F9`/`Ctrl+F9`).
6.  **Run the App**:
    *   Select an Android Emulator (API 24 or higher, as `minSdk = 24`).
    *   Or, connect a physical Android device (with USB Debugging enabled).
    *   Click the "Run 'app'" button (▶️) in Android Studio.

The app uses the public PokeAPI, so no API keys are required. Internet permission (`android.permission.INTERNET`) is declared in `AndroidManifest.xml`.

## Testing

The project includes instrumented UI tests to verify key functionalities of the application. These tests are designed to run on an Android emulator or physical device.

*   **Test Runner**: A custom `HiltTestRunner` (located in `app/src/androidTest/java/com/amiruls/pokedex/HiltTestRunner.kt`) is configured in `app/build.gradle.kts` to enable Hilt in tests:
    ```kotlin
    package com.amiruls.pokedex

    import android.app.Application
    import android.content.Context
    import androidx.test.runner.AndroidJUnitRunner
    import dagger.hilt.android.testing.HiltTestApplication

    class HiltTestRunner : AndroidJUnitRunner() {
        override fun newApplication(cl: ClassLoader?, name: String?, context: Context?): Application {
            return super.newApplication(cl, HiltTestApplication::class.java.name, context)
        }
    }
    ```
*   **Main Test File**: `app/src/androidTest/java/com/amiruls/pokedex/ui/InstrumentedTest.kt`
*   **Core Testing Libraries**:
    *   Jetpack Compose UI Test (`androidx.compose.ui.test.junit4`)
    *   Hilt Android Testing (`hilt-android-testing`) for dependency injection in tests.
    *   JUnit 4
    *   KotlinX Coroutines Test (`kotlinx-coroutines-test`)
*   **Test Approach**:
    *   The tests use `@HiltAndroidTest` to set up a Hilt-enabled testing environment.
    *   `@Inject` is used to get instances of dependencies like `PokemonRepositoryInterface` (providing the real implementation from `AppModule` by default).
    *   Tests interact with the Jetpack Compose UI, performing actions like clicks and verifying displayed data.
*   **Key Test Scenarios Covered in `InstrumentedTest.kt`**:
    *   Verification that the Pokémon list displays correctly (e.g., "Bulbasaur" is visible).
    *   Testing the sorting functionality (by ID, Name A-Z, Name Z-A) and verifying the expected first Pokémon.
    *   Testing navigation to the Pokémon detail screen and checking for specific content (e.g., an ability like "Overgrow").
    *   Testing the "Favorite" feature: marking a Pokémon as a favorite, navigating back, filtering by favorites, and verifying the Pokémon appears in the filtered list.
*   **Running Tests**:
    1.  **Via Android Studio**:
        *   Open `InstrumentedTest.kt`.
        *   Click the green play icon beside the class name or individual test methods.
    2.  **Via Gradle Command Line**:
        ```bash
        ./gradlew app:connectedDebugAndroidTest
        ```
        This command runs all instrumented tests on a connected Android device or emulator.

---
*This README.md was initially drafted with the assistance of an AI programming partner and has been subsequently reviewed and verified by the project developer.*
