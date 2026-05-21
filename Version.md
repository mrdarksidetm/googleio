# Google I/O Project Version History (Version.md)

This file is the absolute source of truth for the project's evolution.

## [1.0.0] - 2026-05-20 15:55:00
- **Status**: 100% (Initial Scaffold)
- **Changes**:
    - Created standalone project structure at `D:\code\GoogleIO`.
    - Configured `build.gradle.kts` with `androidx.compose.material3:material3-android:1.4.0-alpha14`.
    - Implemented `MainActivity.kt` with Material 3 Expressive (M3E) prototype.
    - Set up `AndroidManifest.xml` for `com.google.io` package.
- **Libraries**:
    - Compose BOM: 2024.02.00
    - Material 3: 1.4.0-alpha14 (ExperimentalExpressiveApi)
    - Kotlin: 1.9.22
    - AGP: 8.3.0

## [1.1.0] - 2026-05-20 17:30:00
- **Status**: 100% (Dynamic Architecture Migration)
- **Changes**:
    - Migrated from hardcoded data to MVVM architecture.
    - Introduced `KeynoteRepository` with simulated async data fetching (1.5s delay).
    - Introduced `IoViewModel` with `IoUiState` for reactive state management.
    - Added `CircularProgressIndicator` and `AnimatedContent` for smooth state transitions.
    - Added "Refresh" action to the TopAppBar to demonstrate dynamic data reloading.
- **Architecture**:
    - Data Layer: `com.google.io.data`
    - ViewModels: `com.google.io.ui.viewmodel`

## [1.2.0] - 2026-05-20 18:00:00
- **Status**: 100% (Low-Memory Optimization)
- **Changes**:
    - Optimized gradle.properties for 4GB RAM hardware.
    - Capped Gradle JVM at 1024MB.
    - Enabled org.gradle.configuration-cache and org.gradle.parallel.
    - Enabled incremental compilation for Kotlin and KAPT.

## [1.3.0] - 2026-05-21 10:30:00
- **Status**: 100% (Build System & M3E API Alignment)
- **Changes**:
    - Fixed plugin resolution failure by simplifying `settings.gradle.kts` repository configuration.
    - Migrated build system to use Version Catalog (`libs.versions.toml`) consistently across root and app modules.
    - Updated AGP to 8.7.3 and Kotlin to 2.1.0 for stability and compatibility with Gradle 9.3.1.
    - Added missing `androidx.lifecycle:lifecycle-viewmodel-compose` dependency.
    - Refactored `MainActivity.kt` to align with Material 3 Expressive `1.4.0-alpha14` API changes (Renamed `SplitButton` to `SplitButtonLayout`, updated internal button slots).
    - Fixed `material-icons-extended` Version Catalog accessor.
- **Libraries**:
    - Gradle: 9.3.1
    - AGP: 8.7.3
    - Kotlin: 2.1.0 (with Compose Compiler Plugin)
    - Material 3: 1.4.0-alpha14

## [1.4.0] - 2026-05-21 14:50:00
- **Status**: 100% (Aesthetic & Functional Overhaul)
- **Changes**:
    - Expanded `KeynoteRepository` to include historical data from 2014 to 2026.
    - Created `com.google.io.ui.theme` with a custom Google I/O Dark Palette (Pure Black, Gemini Blue/Purple, and iconic Google brand colors).
    - Implemented `GoogleIoTheme` using `MaterialExpressiveTheme` for a premium, high-contrast look.
    - Added Gemini-inspired animated radial gradient background with pulse effects.
    - Enhanced `AnimatedContent` transitions with combined scale and fade effects.
    - Implemented `SplitButtonLayout` actions: "Watch Keynote" now opens external browser links, and "Share" triggers a native share sheet.
    - Modernized card UI with better typography, spacing, and rollout status badges.
- **Files Created**:
    - `D:\code\GoogleIO\app\src\main\java\com\google\io\ui\theme\Color.kt`
    - `D:\code\GoogleIO\app\src\main\java\com\google\io\ui\theme\Theme.kt`
