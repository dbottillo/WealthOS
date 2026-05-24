# Dark Mode Support for WealthOS

This document outlines the design and implementation plan for dark mode support in WealthOS.

## Color Scheme Configuration

We will use Compose's `isSystemInDarkTheme()` to dynamically detect the macOS system theme (and the browser's theme on Web) and switch MaterialTheme color schemes.

### 1. Dark Theme Color Palette
* **Primary (Accent):** `Color(0xFFD0BCFF)` (M3 Light Pastel Purple)
* **Background:** `Color(0xFF141218)` (Sleek dark slate/purple background)
* **Surface:** `Color(0xFF1D1B20)` (Lighter surface container for card contents)
* **OnSurface / Text:** `Color(0xFFE6E1E5)` (Soft off-white text to avoid eye strain)

### 2. Category Color Mapping (Pastels to Dark Mode)
To keep the visual color semantic groups (purple for income, blue for needs, yellow for wants, green for savings) legible in dark mode, we will define low-intensity dark-theme equivalents:

| Category / Use Case | Light Mode Color | Dark Mode Color |
| --- | --- | --- |
| **Purple (Income)** | `Color(0xFFF3E5F5)` | `Color(0xFF32283D)` |
| **Blue (Needs)** | `Color(0xFFE3F2FD)` | `Color(0xFF1E2D3D)` |
| **Yellow (Wants)** | `Color(0xFFFFFDE7)` | `Color(0xFF38331A)` |
| **Green (Savings)** | `Color(0xFFE8F5E9)` | `Color(0xFF1D3323)` |
| **Red (Critical/Alert)** | `Color(0xFFFFEBEE)` | `Color(0xFF4C2727)` |

---

## Code Adaptations

### 1. Root Theme Implementation (`App.kt`)
Replace `MaterialTheme(colorScheme = lightColorScheme(...))` with a conditional scheme toggled by `isSystemInDarkTheme()`.

### 2. Eliminating Hardcoded Surface/Text Colors
We will scan and replace hardcoded light-only colors with MaterialTheme semantic tokens:
* Replace `Color.White` on card containers and backgrounds with `MaterialTheme.colorScheme.surface`.
* Replace `Color.Black` and `Color.DarkGray` on labels with `MaterialTheme.colorScheme.onSurface` or `MaterialTheme.colorScheme.onSurfaceVariant`.
* Ensure that the `InfoChip` text and detailed table headers use proper contrast colors.

### 3. Chart Adapting to Theme (`AnalyticsDashboard.kt`)
* Modify `SpendingTrendChart` trend line and points color to dynamically use `MaterialTheme.colorScheme.primary` (which transitions to the pastel lavender purple in dark mode) instead of `Color(0xFF6200EE)`.
* Modify `BucketDistributionChart` (pie chart) and its legend items to use desaturated pastel versions in dark mode to match Material Design 3 guidelines:
  * **Needs:** `Color(0xFF90CAF9)` (Dark) vs `Color(0xFF2196F3)` (Light)
  * **Wants:** `Color(0xFFFFE082)` (Dark) vs `Color(0xFFFFC107)` (Light)
  * **Savings:** `Color(0xFF81C784)` (Dark) vs `Color(0xFF4CAF50)` (Light)
* Modify the summary card "Balance" indicator color to use softer green/red values in dark mode:
  * **Positive Balance:** `Color(0xFF81C784)` (Dark) vs `Color(0xFF4CAF50)` (Light)
  * **Negative Balance:** `Color(0xFFE57373)` (Dark) vs `Color(0xFFF44336)` (Light)

### 4. Native macOS Title Bar Appearance (`main.kt` and `build.gradle.kts`)
* Set `System.setProperty("apple.awt.application.appearance", "system")` at the very beginning of `main()` in [main.kt](file:///Users/dbottillo/Development/WealthOS/composeApp/src/jvmMain/kotlin/main.kt).
* Add `jvmArgs += listOf("-Dapple.awt.application.appearance=system")` inside the `application` block of `compose.desktop` in [build.gradle.kts](file:///Users/dbottillo/Development/WealthOS/composeApp/build.gradle.kts) to ensure that the AWT toolkit is initialized with the correct macOS appearance setting at JVM startup.
* Dynamically set the client property `apple.awt.windowAppearance` on the `window.rootPane` in [main.kt](file:///Users/dbottillo/Development/WealthOS/composeApp/src/jvmMain/kotlin/main.kt) using a `LaunchedEffect(isSystemInDarkTheme())` to toggle between `"NSAppearanceNameDarkAqua"` and `"NSAppearanceNameAqua"`.
* Dynamically set the background color of the Swing window and its content pane in [main.kt](file:///Users/dbottillo/Development/WealthOS/composeApp/src/jvmMain/kotlin/main.kt) to match the app theme (`Color(0xFF141218)` in dark mode, `Color(0xFFF6F6F6)` in light mode) to prevent the default white Swing window background from showing through the 28 dp top padding under the transparent macOS title bar.

### 5. Layout Spacing Optimization (`main.kt`)
* Reduce the `topPadding` parameter in the `App` composable initialization inside [main.kt](file:///Users/dbottillo/Development/WealthOS/composeApp/src/jvmMain/kotlin/main.kt) from `28.dp` to `12.dp` to move the app contents closer to the top and reduce the status bar gap.




---

## Verification
* Launch in Light Mode: Verify appearance remains identical to current design.
* Launch in Dark Mode: Toggle macOS system theme and verify full, high-contrast dark theme automatically applies.
