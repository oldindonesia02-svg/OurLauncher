package com.ourlauncher.app

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class GlassMode {
    CLEAR,
    FROSTED,
    TINTED,
    ADAPTIVE
}

class SettingsManager(context: Context) {
    // Glass & Shader Configurations
    var glassMode by mutableStateOf(GlassMode.FROSTED)
    var glassBlurRadius by mutableFloatStateOf(25f)
    var glassTransparency by mutableFloatStateOf(0.85f)
    var glassTintAlpha by mutableFloatStateOf(0.18f)
    var specularHighlight by mutableFloatStateOf(0.65f)
    var enableRainbowSheen by mutableStateOf(true)
    var glassDepthEnabled by mutableStateOf(true)
    var dockCornerRadius by mutableFloatStateOf(32f)

    // Gesture & System Triggers
    var rightPullDownAction by mutableStateOf("Control Center")
    var leftPullDownAction by mutableStateOf("Notifications")
    var isControlCenterEnabled by mutableStateOf(true)
}
