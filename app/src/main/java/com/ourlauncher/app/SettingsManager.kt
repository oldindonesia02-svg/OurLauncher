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

    // Desktop Grid
    var gridColumns by mutableIntStateOf(prefs.getInt("grid_columns", 4))
    var gridRows by mutableIntStateOf(prefs.getInt("grid_rows", 5))

    // App Icons
    var iconSize by mutableFloatStateOf(prefs.getFloat("icon_size", 62f))
    var iconCornerRadius by mutableFloatStateOf(prefs.getFloat("icon_corner_radius", 28f))
    var iconOpacity by mutableFloatStateOf(prefs.getFloat("icon_opacity", 1.0f))
    var showLabels by mutableStateOf(prefs.getBoolean("show_labels", true))
    var fontFamily by mutableStateOf(prefs.getString("font_family", "SF Pro") ?: "SF Pro")

    // Search Capsule
    var searchOffset by mutableFloatStateOf(prefs.getFloat("search_offset", 0f))
    var hideSearchCapsule by mutableStateOf(prefs.getBoolean("hide_search", false))

    // Liquid Dock
    var dockCapacity by mutableIntStateOf(prefs.getInt("dock_capacity", 4))
    var dockCornerRadius by mutableFloatStateOf(prefs.getFloat("dock_radius", 28f))
    var dockGlassOpacity by mutableFloatStateOf(prefs.getFloat("dock_opacity", 0.94f))
    var dockSpecularGlow by mutableStateOf(prefs.getBoolean("dock_glow", true))

    // Blur & Animations
    var windowBlurRadius by mutableFloatStateOf(prefs.getFloat("window_blur", 22f))
    var animationSpeed by mutableStateOf(prefs.getString("anim_speed", "Smooth (300ms)") ?: "Smooth (300ms)")

    // Gestures & Swipe Actions
    var doubleTapAction by mutableStateOf(prefs.getString("double_tap", "Lock Screen") ?: "Lock Screen")
    var leftPullDownAction by mutableStateOf(prefs.getString("left_pull_down", "Notifications") ?: "Notifications")
    var rightPullDownAction by mutableStateOf(prefs.getString("right_pull_down", "Control Center") ?: "Control Center")
    var swipeUpAction by mutableStateOf(prefs.getString("swipe_up", "App Drawer") ?: "App Drawer")
    var swipeDownAction by mutableStateOf(prefs.getString("swipe_down", "Search") ?: "Search")

    // Liquid Glass Advanced Features & References
    var glassBlurRadius by mutableFloatStateOf(prefs.getFloat("glass_blur_radius", 24f))
    var glassTransparency by mutableFloatStateOf(prefs.getFloat("glass_transparency", 0.65f))
    var glassRefractionHeight by mutableFloatStateOf(prefs.getFloat("glass_refraction_height", 24f))
    var glassRefractionAmount by mutableFloatStateOf(prefs.getFloat("glass_refraction_amount", 0.5f))
    var glassDepthEnabled by mutableStateOf(prefs.getBoolean("glass_depth_enabled", true))
    var glassTintAlpha by mutableFloatStateOf(prefs.getFloat("glass_tint_alpha", 0.65f))
    var specularHighlight by mutableFloatStateOf(prefs.getFloat("specular_highlight", 0.85f))
    var enableRainbowSheen by mutableStateOf(prefs.getBoolean("rainbow_sheen", true))

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
            putString("left_pull_down", leftPullDownAction)
            putString("right_pull_down", rightPullDownAction)
            putString("swipe_up", swipeUpAction)
            putString("swipe_down", swipeDownAction)
            putFloat("glass_blur_radius", glassBlurRadius)
            putFloat("glass_transparency", glassTransparency)
            putFloat("glass_refraction_height", glassRefractionHeight)
            putFloat("glass_refraction_amount", glassRefractionAmount)
            putBoolean("glass_depth_enabled", glassDepthEnabled)
            putFloat("glass_tint_alpha", glassTintAlpha)
            putFloat("specular_highlight", specularHighlight)
            putBoolean("rainbow_sheen", enableRainbowSheen)
            apply()
        }
    }
}
