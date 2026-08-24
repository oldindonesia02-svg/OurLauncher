package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("void_settings", Context.MODE_PRIVATE)

    var showLabels: Boolean
        get() = prefs.getBoolean("show_labels", true)
        set(value) = prefs.edit().putBoolean("show_labels", value).apply()

    var dockRadius: Float
        get() = prefs.getFloat("dock_radius", 32f)
        set(value) = prefs.edit().putFloat("dock_radius", value).apply()

    var showDockBg: Boolean
        get() = prefs.getBoolean("show_dock_bg", true)
        set(value) = prefs.edit().putBoolean("show_dock_bg", value).apply()

    var searchOffset: Float
        get() = prefs.getFloat("search_offset", 0f)
        set(value) = prefs.edit().putFloat("search_offset", value).apply()

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

    var swipeUpAction: String
        get() = prefs.getString("swipe_up", "drawer") ?: "drawer"
        set(value) = prefs.edit().putString("swipe_up", value).apply()

    var swipeDownAction: String
        get() = prefs.getString("swipe_down", "none") ?: "none"
        set(value) = prefs.edit().putString("swipe_down", value).apply()

    var swipeLeftAction: String
        get() = prefs.getString("swipe_left", "none") ?: "none"
        set(value) = prefs.edit().putString("swipe_left", value).apply()

    var swipeRightAction: String
        get() = prefs.getString("swipe_right", "none") ?: "none"
        set(value) = prefs.edit().putString("swipe_right", value).apply()
}
