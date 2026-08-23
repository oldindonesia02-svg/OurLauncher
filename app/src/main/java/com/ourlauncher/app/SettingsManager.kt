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
}
