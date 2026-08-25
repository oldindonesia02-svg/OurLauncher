package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ourlauncher_settings", Context.MODE_PRIVATE)

    // App & Layout Settings
    var showLabels: Boolean
        get() = prefs.getBoolean("show_labels", true)
        set(value) = prefs.edit().putBoolean("show_labels", value).apply()

    var iconSize: Float
        get() = prefs.getFloat("icon_size", 56f)
        set(value) = prefs.edit().putFloat("icon_size", value).apply()

    var iconCornerRadius: Float
        get() = prefs.getFloat("icon_corner_radius", 22f)
        set(value) = prefs.edit().putFloat("icon_corner_radius", value).apply()

    // Search Bar Settings
    var searchBarOffset: Float
        get() = prefs.getFloat("search_bar_offset", 0f)
        set(value) = prefs.edit().putFloat("search_bar_offset", value).apply()

    var isSearchCapsuleHidden: Boolean
        get() = prefs.getBoolean("is_search_capsule_hidden", false)
        set(value) = prefs.edit().putBoolean("is_search_capsule_hidden", value).apply()

    // Swipe Gestures
    var leftPullDownAction: String
        get() = prefs.getString("left_pull_down_action", "Notifications") ?: "Notifications"
        set(value) = prefs.edit().putString("left_pull_down_action", value).apply()

    var rightPullDownAction: String
        get() = prefs.getString("right_pull_down_action", "Quick Settings") ?: "Quick Settings"
        set(value) = prefs.edit().putString("right_pull_down_action", value).apply()

    // Liquid Glass Settings
    var glassBlurRadius: Float
        get() = prefs.getFloat("glass_blur_radius", 20f)
        set(value) = prefs.edit().putFloat("glass_blur_radius", value).apply()

    var glassRefractionHeight: Float
        get() = prefs.getFloat("glass_refraction_height", 1.5f)
        set(value) = prefs.edit().putFloat("glass_refraction_height", value).apply()

    var glassRefractionAmount: Float
        get() = prefs.getFloat("glass_refraction_amount", 50f)
        set(value) = prefs.edit().putFloat("glass_refraction_amount", value).apply()

    var glassDepthEnabled: Boolean
        get() = prefs.getBoolean("glass_depth_enabled", true)
        set(value) = prefs.edit().putBoolean("glass_depth_enabled", value).apply()
}
