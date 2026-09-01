package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("launcher_settings", Context.MODE_PRIVATE)

    // Reactive Compose States with Auto SharedPreferences Save
    var gridColumns by mutableIntStateOf(prefs.getInt("grid_columns", 4))
    var gridRows by mutableIntStateOf(prefs.getInt("grid_rows", 5))

    var iconSize by mutableFloatStateOf(prefs.getFloat("icon_size", 62f))
    var iconCornerRadius by mutableFloatStateOf(prefs.getFloat("icon_corner_radius", 28f))
    var iconOpacity by mutableFloatStateOf(prefs.getFloat("icon_opacity", 1.0f))
    var showLabels by mutableStateOf(prefs.getBoolean("show_labels", true))
    var fontFamily by mutableStateOf(prefs.getString("font_family", "SF Pro") ?: "SF Pro")

    var searchOffset by mutableFloatStateOf(prefs.getFloat("search_offset", 0f))
    var hideSearchCapsule by mutableStateOf(prefs.getBoolean("hide_search", false))

    var dockCapacity by mutableIntStateOf(prefs.getInt("dock_capacity", 4))
    var dockCornerRadius by mutableFloatStateOf(prefs.getFloat("dock_radius", 28f))
    var dockGlassOpacity by mutableFloatStateOf(prefs.getFloat("dock_opacity", 0.94f))
    var dockSpecularGlow by mutableStateOf(prefs.getBoolean("dock_glow", true))

    var windowBlurRadius by mutableFloatStateOf(prefs.getFloat("window_blur", 22f))
    var animationSpeed by mutableStateOf(prefs.getString("anim_speed", "Smooth (300ms)") ?: "Smooth (300ms)")
    var doubleTapAction by mutableStateOf(prefs.getString("double_tap", "Lock Screen") ?: "Lock Screen")

    fun saveAll() {
        prefs.edit().apply {
            putInt("grid_columns", gridColumns)
            putInt("grid_rows", gridRows)
            putFloat("icon_size", iconSize)
            putFloat("icon_corner_radius", iconCornerRadius)
            putFloat("icon_opacity", iconOpacity)
            putBoolean("show_labels", showLabels)
            putString("font_family", fontFamily)
            putFloat("search_offset", searchOffset)
            putBoolean("hide_search", hideSearchCapsule)
            putInt("dock_capacity", dockCapacity)
            putFloat("dock_radius", dockCornerRadius)
            putFloat("dock_opacity", dockGlassOpacity)
            putBoolean("dock_glow", dockSpecularGlow)
            putFloat("window_blur", windowBlurRadius)
            putString("anim_speed", animationSpeed)
            putString("double_tap", doubleTapAction)
            apply()
        }
    }
}
