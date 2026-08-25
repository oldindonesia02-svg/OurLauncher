package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ourlauncher_settings", Context.MODE_PRIVATE)

    // ================= App & Icons =================
    var showLabels: Boolean
        get() = prefs.getBoolean("show_labels", true)
        set(value) = prefs.edit().putBoolean("show_labels", value).apply()

    var fontFamily: String
        get() = prefs.getString("font_family", "Default") ?: "Default"
        set(value) = prefs.edit().putString("font_family", value).apply()

    var iconSize: Float
        get() = prefs.getFloat("icon_size", 56f)
        set(value) = prefs.edit().putFloat("icon_size", value).apply()

    var iconCornerRadius: Float
        get() = prefs.getFloat("icon_corner_radius", 22f)
        set(value) = prefs.edit().putFloat("icon_corner_radius", value).apply()

    var iconOpacity: Float
        get() = prefs.getFloat("icon_opacity", 1.0f)
        set(value) = prefs.edit().putFloat("icon_opacity", value).apply()

    // ================= Lens, Lighting & Graphics =================
    var lensLightEnabled: Boolean
        get() = prefs.getBoolean("lens_light_enabled", true)
        set(value) = prefs.edit().putBoolean("lens_light_enabled", value).apply()

    var graphicPreset: String
        get() = prefs.getString("graphic_preset", "Highlight") ?: "Highlight"
        set(value) = prefs.edit().putString("graphic_preset", value).apply()

    var lensStrokeWidth: Float
        get() = prefs.getFloat("lens_stroke_width", 1.5f)
        set(value) = prefs.edit().putFloat("lens_stroke_width", value).apply()

    var lensBlur: Float
        get() = prefs.getFloat("lens_blur", 0.5f)
        set(value) = prefs.edit().putFloat("lens_blur", value).apply()

    var lensFalloff: Float
        get() = prefs.getFloat("lens_falloff", 1.5f)
        set(value) = prefs.edit().putFloat("lens_falloff", value).apply()

    var lensIntensity: Float
        get() = prefs.getFloat("lens_intensity", 100f)
        set(value) = prefs.edit().putFloat("lens_intensity", value).apply()

    var lensAngle: Float
        get() = prefs.getFloat("lens_angle", 75f)
        set(value) = prefs.edit().putFloat("lens_angle", value).apply()

    // ================= Dock Settings =================
    var showDockBg: Boolean
        get() = prefs.getBoolean("show_dock_bg", true)
        set(value) = prefs.edit().putBoolean("show_dock_bg", value).apply()

    var dockPadding: Float
        get() = prefs.getFloat("dock_padding", 12f)
        set(value) = prefs.edit().putFloat("dock_padding", value).apply()

    var dockGap: Float
        get() = prefs.getFloat("dock_gap", 8f)
        set(value) = prefs.edit().putFloat("dock_gap", value).apply()

    var dockCornerRadius: Float
        get() = prefs.getFloat("dock_corner_radius", 24f)
        set(value) = prefs.edit().putFloat("dock_corner_radius", value).apply()

    // ================= Search Bar =================
    var searchBarOffset: Float
        get() = prefs.getFloat("search_bar_offset", 0f)
        set(value) = prefs.edit().putFloat("search_bar_offset", value).apply()

    var isSearchCapsuleHidden: Boolean
        get() = prefs.getBoolean("is_search_capsule_hidden", false)
        set(value) = prefs.edit().putBoolean("is_search_capsule_hidden", value).apply()

    // ================= Swipe Actions =================
    var leftPullDownAction: String
        get() = prefs.getString("left_pull_down_action", "Notifications") ?: "Notifications"
        set(value) = prefs.edit().putString("left_pull_down_action", value).apply()

    var rightPullDownAction: String
        get() = prefs.getString("right_pull_down_action", "Quick Settings") ?: "Quick Settings"
        set(value) = prefs.edit().putString("right_pull_down_action", value).apply()

    // ================= Liquid Glass =================
    var glassMode: String
        get() = prefs.getString("glass_mode", "Liquid") ?: "Liquid"
        set(value) = prefs.edit().putString("glass_mode", value).apply()

    var glassTransparency: Float
        get() = prefs.getFloat("glass_transparency", 0.35f)
        set(value) = prefs.edit().putFloat("glass_transparency", value).apply()

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
