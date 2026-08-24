package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("void_settings", Context.MODE_PRIVATE)

    // Layout Persistence
    var homeGridApps: List<String>
        get() = prefs.getString("home_grid_apps", "")?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        set(value) = prefs.edit().putString("home_grid_apps", value.joinToString(",")).apply()

    // Live Search Bar Position Settings
    var searchOffset: Float
        get() = prefs.getFloat("search_offset", 0f)
        set(value) = prefs.edit().putFloat("search_offset", value).apply()

    var hideSearchCapsule: Boolean
        get() = prefs.getBoolean("hide_search_capsule", false)
        set(value) = prefs.edit().putBoolean("hide_search_capsule", value).apply()

    // Live Dock Customization Settings
    var dockRadius: Float
        get() = prefs.getFloat("dock_radius", 33f)
        set(value) = prefs.edit().putFloat("dock_radius", value).apply()

    var dockOffset: Float
        get() = prefs.getFloat("dock_offset", 0f)
        set(value) = prefs.edit().putFloat("dock_offset", value).apply()

    var showDockBg: Boolean
        get() = prefs.getBoolean("show_dock_bg", true)
        set(value) = prefs.edit().putBoolean("show_dock_bg", value).apply()

    // General Customization
    var showLabels: Boolean
        get() = prefs.getBoolean("show_labels", true)
        set(value) = prefs.edit().putBoolean("show_labels", value).apply()

    var fontFamily: String
        get() = prefs.getString("font_family", "sans-serif") ?: "sans-serif"
        set(value) = prefs.edit().putString("font_family", value).apply()

    var iconSize: Float
        get() = prefs.getFloat("icon_size", 54f)
        set(value) = prefs.edit().putFloat("icon_size", value).apply()

    var iconCornerRadius: Float
        get() = prefs.getFloat("icon_corner_radius", 25f)
        set(value) = prefs.edit().putFloat("icon_corner_radius", value).apply()

    var iconOpacity: Float
        get() = prefs.getFloat("icon_opacity", 1.0f)
        set(value) = prefs.edit().putFloat("icon_opacity", value).apply()

    var iconPack: String
        get() = prefs.getString("icon_pack", "default") ?: "default"
        set(value) = prefs.edit().putString("icon_pack", value).apply()

    // Gesture Actions
    var leftPullDownAction: String
        get() = prefs.getString("left_pull_down", "notifications") ?: "notifications"
        set(value) = prefs.edit().putString("left_pull_down", value).apply()

    var rightPullDownAction: String
        get() = prefs.getString("right_pull_down", "system_control_center") ?: "system_control_center"
        set(value) = prefs.edit().putString("right_pull_down", value).apply()

    // Liquid Glass Properties
    var glassMode: String
        get() = prefs.getString("glass_mode", "easy") ?: "easy"
        set(value) = prefs.edit().putString("glass_mode", value).apply()

    var glassTransparency: Float
        get() = prefs.getFloat("glass_transparency", 0.15f)
        set(value) = prefs.edit().putFloat("glass_transparency", value).apply()

    var glassBlurRadius: Float
        get() = prefs.getFloat("glass_blur_radius", 0.30f)
        set(value) = prefs.edit().putFloat("glass_blur_radius", value).apply()

    var glassRefractionHeight: Float
        get() = prefs.getFloat("glass_refraction_height", 20f)
        set(value) = prefs.edit().putFloat("glass_refraction_height", value).apply()

    var glassRefractionAmount: Float
        get() = prefs.getFloat("glass_refraction_amount", 35f)
        set(value) = prefs.edit().putFloat("glass_refraction_amount", value).apply()

    var glassDepthEnabled: Boolean
        get() = prefs.getBoolean("glass_depth_enabled", false)
        set(value) = prefs.edit().putBoolean("glass_depth_enabled", value).apply()

    // App Open Animation
    var animEnabled: Boolean
        get() = prefs.getBoolean("anim_enabled", true)
        set(value) = prefs.edit().putBoolean("anim_enabled", value).apply()

    var animAdvancedTexture: Boolean
        get() = prefs.getBoolean("anim_adv_texture", false)
        set(value) = prefs.edit().putBoolean("anim_adv_texture", value).apply()

    var animDuration: Float
        get() = prefs.getFloat("anim_duration", 300f)
        set(value) = prefs.edit().putFloat("anim_duration", value).apply()

    var posCurveX1: Float get() = prefs.getFloat("pos_x1", 0.25f); set(v) = prefs.edit().putFloat("pos_x1", v).apply()
    var posCurveY1: Float get() = prefs.getFloat("pos_y1", 0.50f); set(v) = prefs.edit().putFloat("pos_y1", v).apply()
    var posCurveX2: Float get() = prefs.getFloat("pos_x2", 0.00f); set(v) = prefs.edit().putFloat("pos_x2", v).apply()
    var posCurveY2: Float get() = prefs.getFloat("pos_y2", 1.00f); set(v) = prefs.edit().putFloat("pos_y2", v).apply()

    var widthCurveX1: Float get() = prefs.getFloat("w_x1", 0.15f); set(v) = prefs.edit().putFloat("w_x1", v).apply()
    var widthCurveY1: Float get() = prefs.getFloat("w_y1", 0.10f); set(v) = prefs.edit().putFloat("w_y1", v).apply()
    var widthCurveX2: Float get() = prefs.getFloat("w_x2", 0.15f); set(v) = prefs.edit().putFloat("w_x2", v).apply()
    var widthCurveY2: Float get() = prefs.getFloat("w_y2", 1.00f); set(v) = prefs.edit().putFloat("w_y2", v).apply()

    var heightCurveX1: Float get() = prefs.getFloat("h_x1", 0.30f); set(v) = prefs.edit().putFloat("h_x1", v).apply()
    var heightCurveY1: Float get() = prefs.getFloat("h_y1", 0.10f); set(v) = prefs.edit().putFloat("h_y1", v).apply()
    var heightCurveX2: Float get() = prefs.getFloat("h_x2", 0.15f); set(v) = prefs.edit().putFloat("h_x2", v).apply()
    var heightCurveY2: Float get() = prefs.getFloat("h_y2", 1.00f); set(v) = prefs.edit().putFloat("h_y2", v).apply()

    var cornerCurveX1: Float get() = prefs.getFloat("c_x1", 0.30f); set(v) = prefs.edit().putFloat("c_x1", v).apply()
    var cornerCurveY1: Float get() = prefs.getFloat("c_y1", 0.00f); set(v) = prefs.edit().putFloat("c_y1", v).apply()
    var cornerCurveX2: Float get() = prefs.getFloat("c_x2", 1.00f); set(v) = prefs.edit().putFloat("c_x2", v).apply()
    var cornerCurveY2: Float get() = prefs.getFloat("c_y2", 0.20f); set(v) = prefs.edit().putFloat("c_y2", v).apply()
}
