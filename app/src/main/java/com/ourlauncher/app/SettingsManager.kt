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
    var graphicPreset by mutableStateOf("Default")

    // Lens & Folder Shader Configurations
    var lensAngle by mutableFloatStateOf(45f)
    var lensIntensity by mutableFloatStateOf(0.7f)
    var lensStrokeWidth by mutableFloatStateOf(1.5f)
    var lensLightEnabled by mutableStateOf(true)

    // Dock Customizations
    var showDockBg by mutableStateOf(true)
    var dockRadius by mutableFloatStateOf(32f)
    var dockCornerRadius by mutableFloatStateOf(32f)
    var dockCapacity by mutableIntStateOf(4)
    var dockGlassOpacity by mutableFloatStateOf(0.8f)
    var dockSpecularGlow by mutableStateOf(true)
    var dockOffset by mutableFloatStateOf(0f)

    // Typography & Icon Customizations
    var fontFamily by mutableStateOf("System")
    var showLabels by mutableStateOf(true)
    var showAppLabels by mutableStateOf(true)
    var iconSize by mutableFloatStateOf(60f)
    var iconOpacity by mutableFloatStateOf(1.0f)
    var iconCornerRadius by mutableFloatStateOf(16f)
    var iconShape by mutableStateOf("Squircle")
    var iconPack by mutableStateOf("Default")

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
            putString("graphicPreset", graphicPreset)
            putFloat("lensAngle", lensAngle)
            putFloat("lensIntensity", lensIntensity)
            putFloat("lensStrokeWidth", lensStrokeWidth)
            putBoolean("lensLightEnabled", lensLightEnabled)
            putBoolean("showDockBg", showDockBg)
            putFloat("dockRadius", dockRadius)
            putFloat("dockCornerRadius", dockCornerRadius)
            putInt("dockCapacity", dockCapacity)
            putFloat("dockGlassOpacity", dockGlassOpacity)
            putBoolean("dockSpecularGlow", dockSpecularGlow)
            putFloat("dockOffset", dockOffset)
            putString("fontFamily", fontFamily)
            putBoolean("showLabels", showLabels)
            putBoolean("showAppLabels", showAppLabels)
            putFloat("iconSize", iconSize)
            putFloat("iconOpacity", iconOpacity)
            putFloat("iconCornerRadius", iconCornerRadius)
            putString("iconShape", iconShape)
            putString("iconPack", iconPack)
            putBoolean("hideSearchCapsule", hideSearchCapsule)
            putFloat("searchOffset", searchOffset)
            putString("rightPullDownAction", rightPullDownAction)
            putString("leftPullDownAction", leftPullDownAction)
            putString("doubleTapAction", doubleTapAction)
            putBoolean("isControlCenterEnabled", isControlCenterEnabled)
            putString("animationSpeed", animationSpeed)
            putInt("gridColumns", gridColumns)
            putInt("gridRows", gridRows)
            apply()
        }
    }
}
