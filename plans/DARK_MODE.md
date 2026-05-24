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

---

## Verification
* Launch in Light Mode: Verify appearance remains identical to current design.
* Launch in Dark Mode: Toggle macOS system theme and verify full, high-contrast dark theme automatically applies.
