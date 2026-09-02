package com.ourlauncher.app

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class GlassMode {
    CLEAR,
    FROSTED,
    TINTED,
    ADAPTIVE
}

class SettingsManager(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("our_launcher_prefs", Context.MODE_PRIVATE)

    // Glass & Shader Configurations
    var glassMode by mutableStateOf(GlassMode.FROSTED)
    var glassBlurRadius by mutableFloatStateOf(25f)
    var windowBlurRadius by mutableFloatStateOf(20f)
    var glassTransparency by mutableFloatStateOf(0.85f)
    var glassTintAlpha by mutableFloatStateOf(0.18f)
    var specularHighlight by mutableFloatStateOf(0.65f)
    var enableRainbowSheen by mutableStateOf(true)
    var glassDepthEnabled by mutableStateOf(true)

    // Dock Customizations
    var dockCornerRadius by mutableFloatStateOf(32f)
    var dockCapacity by mutableIntStateOf(4)
    var dockGlassOpacity by mutableFloatStateOf(0.8f)
    var dockSpecularGlow by mutableStateOf(true)

    // Typography & Icon Labels
    var fontFamily by mutableStateOf("System")
    var showLabels by mutableStateOf(true)
    var showAppLabels by mutableStateOf(true)

    // Search Capsule & Layout Offsets
    var hideSearchCapsule by mutableStateOf(false)
    var searchOffset by mutableFloatStateOf(0f)

    // Gesture & System Controls
    var rightPullDownAction by mutableStateOf("Control Center")
    var leftPullDownAction by mutableStateOf("Notifications")
    var doubleTapAction by mutableStateOf("Lock Screen")
    var isControlCenterEnabled by mutableStateOf(true)

    // Animation & Performance
    var animationSpeed by mutableStateOf("Normal")

    // UI Grid Customization
    var iconSize by mutableFloatStateOf(60f)
    var gridColumns by mutableIntStateOf(4)
    var gridRows by mutableIntStateOf(5)

    fun saveAll() {
        prefs.edit().apply {
            putString("glassMode", glassMode.name)
            putFloat("glassBlurRadius", glassBlurRadius)
            putFloat("windowBlurRadius", windowBlurRadius)
            putFloat("glassTransparency", glassTransparency)
            putFloat("glassTintAlpha", glassTintAlpha)
            putFloat("specularHighlight", specularHighlight)
            putBoolean("enableRainbowSheen", enableRainbowSheen)
            putBoolean("glassDepthEnabled", glassDepthEnabled)
            putFloat("dockCornerRadius", dockCornerRadius)
            putInt("dockCapacity", dockCapacity)
            putFloat("dockGlassOpacity", dockGlassOpacity)
            putBoolean("dockSpecularGlow", dockSpecularGlow)
            putString("fontFamily", fontFamily)
            putBoolean("showLabels", showLabels)
            putBoolean("showAppLabels", showAppLabels)
            putBoolean("hideSearchCapsule", hideSearchCapsule)
            putFloat("searchOffset", searchOffset)
            putString("rightPullDownAction", rightPullDownAction)
            putString("leftPullDownAction", leftPullDownAction)
            putString("doubleTapAction", doubleTapAction)
            putBoolean("isControlCenterEnabled", isControlCenterEnabled)
            putString("animationSpeed", animationSpeed)
            putFloat("iconSize", iconSize)
            putInt("gridColumns", gridColumns)
            putInt("gridRows", gridRows)
            apply()
        }
    }
}
