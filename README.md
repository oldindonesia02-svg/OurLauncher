# Our Launcher — Phase 1

A real, installable Android launcher skeleton: Kotlin + Jetpack Compose.

## What works right now
- Registers as a selectable launcher (`HOME` intent filter) — after installing, Android
  will offer it in the "select launcher" dialog, or you can set it under
  **Settings → Apps → Default apps → Home app**.
- Shows the real system wallpaper behind the UI (`windowShowWallpaper`).
- Reads all installed apps via `LauncherApps` (the correct API for launchers).
- Home screen: fixed grid of apps + a 4-app dock + a search pill.
- Swipe up anywhere on the home screen (or tap the search pill) → opens the app drawer.
- App drawer: live search-as-you-type over all installed apps, tap to launch.
- Back button/gesture closes the drawer.

## What's intentionally NOT here yet (see roadmap)
- Multiple home pages / drag-to-reorder / folders / widgets → Phase 2 & 7
- Swipe gesture customization, Control Center → Phase 3
- Lock screen, passcode, fake fingerprint → Phase 4
- Icon shape/opacity/icon-packs, themes → Phase 5
- Liquid Glass blur/transparency/depth → Phase 6
- Custom animation-curve engine → Phase 8

## How to build
1. Open this folder in **Android Studio** (Koala/2024.1 or newer recommended).
2. Let Gradle sync — it will pull the AGP 8.5.2 / Kotlin 1.9.24 / Compose BOM 2024.06
   versions pinned in `build.gradle.kts` files.
3. Run on a device or emulator running **Android 8.0 (API 26) or newer**.
4. On first launch, Android will ask if you want to use it as your Home app —
   choose "Always" or "Just once" to test. You can switch back anytime from
   Settings → Apps → Default apps → Home app.

## Project layout
```
app/src/main/java/com/ourlauncher/app/
  MainActivity.kt        — hosts Compose content, switches Home ⇄ Drawer
  AppRepository.kt        — LauncherApps wrapper: list + launch apps
  IconUtils.kt             — Drawable → ImageBitmap conversion
  OurLauncherApp.kt        — Application class (empty for now, DI home later)
  ui/
    HomeScreen.kt          — grid + dock + search pill + swipe-up gesture
    AppDrawer.kt           — full list + live search
    Dock.kt                — bottom pinned-app bar (flat, pre-Liquid-Glass)
    AppIcon.kt              — shared icon+label item
    SearchPill.kt           — the pill button/bar
```

## Next up: Phase 2
Multi-page home screen, drag-and-drop reorder, folders, and a proper Settings
screen shell matching the nested search-settings UI from the reference app.
