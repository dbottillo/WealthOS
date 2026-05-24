# macOS App Target for WealthOS

This document details the configuration and design of the macOS native application target for WealthOS.

## Architecture & Code Reuse

Because WealthOS is built on top of **Kotlin Multiplatform (KMP)** and **Compose Multiplatform**, we reuse **100%** of the shared code across Android, Web, and macOS targets:
* **Business Logic & ViewModels:** Contained in the `:common` module, using shared `Ktor Client` for networking and `Koin` for dependency injection.
* **UI Components:** Contained in `composeApp/src/commonMain`, utilizing Compose Material 3 components.

---

## macOS Specific Features

### 1. Transparent Title Bar & Traffic Lights
To create a modern, borderless macOS application, we enabled AWT/Swing client properties in `main.kt` before drawing content:
* `apple.awt.fullWindowContent = true` (extends Compose canvas behind the title bar area).
* `apple.awt.transparentTitleBar = true` (makes the title bar transparent).
* `apple.awt.windowTitleVisible = false` (hides the default window title text).

To prevent window elements from overlapping with the macOS traffic lights (close, minimize, maximize buttons), we added an optional `topPadding = 28.dp` parameter to the main `App` composable.

### 2. Native System Menu Bar
Instead of placing a settings cog on the UI screen, we integrated navigation directly into the macOS system menu bar using Compose Desktop's `MenuBar` DSL:
* **Navigation Menu:** Adds items for "Overview", "Analytics", and "Manage Categories".
* **State Hoisting:** The menu bar items change a hoisted `currentScreen` state inside `Window`, driving the active screen in the shared `App` layout.

### 3. Application Branding & Icon
The macOS target packages with a custom-designed purple app icon featuring a white "Wo" emblem. The icon has transparent rounded corners to fit cleanly into the macOS Dock grid.

---

## Technical Configuration

### Dependencies (`composeApp/build.gradle.kts`)
* `implementation(compose.desktop.currentOs)` provides native graphics libraries (Skiko).
* `implementation(libs.ktor.client.okhttp)` provides the JVM-specific HTTP client engine.
* `implementation(libs.kotlinx.coroutines.swing)` provides `Dispatchers.Main` mapped to the Swing Event Dispatch Thread (EDT) for coroutines execution.

---

## Verification & Commands

### 1. Local Run (Debug)
Compiles and launches the desktop app locally, injecting `WEALTHOS_API_URL=http://localhost:8080` for local backend development:
```bash
./gradlew :composeApp:run
```

### 2. Packaging (Release)
Generates a native `.dmg` installer package in `composeApp/build/compose/binaries/main/dmg/`. In release mode, the app defaults to your Cloudflare production tunnel domain (`https://wealthos.bottillo.com`):
```bash
./gradlew :composeApp:packageDmg
```
