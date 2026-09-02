package com.ourlauncher.app.ui.controlcenter

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import com.ourlauncher.app.ui.components.LiquidGlassSurface
import com.ourlauncher.app.ui.components.liquidGlassEffect

@Composable
fun ControlCenterOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    settings: SettingsManager,
    isDarkTheme: Boolean = true
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    var flashlightOn by remember { mutableStateOf(false) }
    var brightness by remember { mutableFloatStateOf(0.75f) }
    var volume by remember { 
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) current / max else 0f) 
    }

    // সেফটি ফাংশন: যাতে কোনো ফোনে সেটিংস ওপেন হতে সমস্যা না হয়
    fun launchSetting(action: String) {
        try {
            val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(300)) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(250)) + fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(onClick = onDismiss) // বাইরে ক্লিক করলে বন্ধ হবে
                // নিচ থেকে উপরে সোয়াইপ করলে বন্ধ হওয়ার লজিক
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20) { // Swipe UP
                            onDismiss()
                        }
                    }
                }
                .statusBarsPadding()
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {} // প্যানেলের ভেতর ক্লিক করলে যাতে বন্ধ না হয়
                    .liquidGlassEffect(settings = settings, cornerRadius = 36.dp, isDarkTheme = isDarkTheme)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Pull Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 42.dp, height = 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.35f))
                )

                // Row 1: Connectivity & Media
                Row(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // System Actions Matrix (Real Intents)
                    LiquidGlassSurface(
                        settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme, modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(modifier = Modifier.fillMaxSize().padding(10.dp), verticalArrangement = Arrangement.SpaceEvenly) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                // Airplane Mode
                                ControlIconButton(
                                    icon = Icons.Default.AirplanemodeActive,
                                    active = false,
                                    onClick = { launchSetting(Settings.ACTION_AIRPLANE_MODE_SETTINGS) }
                                )
                                // Mobile Data
                                ControlIconButton(
                                    icon = Icons.Default.SignalCellularAlt,
                                    active = true,
                                    onClick = { launchSetting(Settings.ACTION_NETWORK_OPERATOR_SETTINGS) }
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                // Wi-Fi
                                ControlIconButton(
                                    icon = Icons.Default.Wifi,
                                    active = true,
                                    onClick = { launchSetting(Settings.ACTION_WIFI_SETTINGS) }
                                )
                                // Bluetooth
                                ControlIconButton(
                                    icon = Icons.Default.Bluetooth,
                                    active = true,
                                    onClick = { launchSetting(Settings.ACTION_BLUETOOTH_SETTINGS) }
                                )
                            }
                        }
                    }

                    // Media Player Module
                    LiquidGlassSurface(
                        settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme, modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("NOT PLAYING", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, letterSpacing = 1.sp)
                            Text("Our Launcher", color = Color.White, fontSize = 14.sp)
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SkipPrevious, null, tint = Color.White.copy(alpha = 0.7f))
                                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                Icon(Icons.Default.SkipNext, null, tint = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    }
                }

                // Row 2: Sliders & Utils
                Row(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // Flashlight
                        LiquidGlassSurface(
                            settings = settings, cornerRadius = 20.dp, isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth().weight(1f),
                            onClick = { flashlightOn = !flashlightOn }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.FlashlightOn, null, tint = if (flashlightOn) Color(0xFF007AFF) else Color.White)
                            }
                        }
                        // Settings Shortcut
                        LiquidGlassSurface(
                            settings = settings, cornerRadius = 20.dp, isDarkTheme = isDarkTheme, modifier = Modifier.fillMaxWidth().weight(1f),
                            onClick = { launchSetting(Settings.ACTION_SETTINGS) }
                        ) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Settings, null, tint = Color.White)
                            }
                        }
                    }

                    // Sliders
                    LiquidGlassSlider(
                        value = brightness, onValueChange = { brightness = it }, icon = Icons.Default.WbSunny,
                        settings = settings, isDarkTheme = isDarkTheme, modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                    LiquidGlassSlider(
                        value = volume, 
                        onValueChange = { 
                            volume = it
                            val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * max).toInt(), 0)
                        }, 
                        icon = Icons.Default.VolumeUp, settings = settings, isDarkTheme = isDarkTheme, modifier = Modifier.weight(1f).fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
fun ControlIconButton(icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.12f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
    }
}

@Composable
fun LiquidGlassSlider(
    value: Float, onValueChange: (Float) -> Unit, icon: ImageVector,
    settings: SettingsManager, isDarkTheme: Boolean, modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme,
        modifier = modifier.pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                change.consume()
                val delta = -dragAmount / size.height
                val newValue = (value + delta).coerceIn(0f, 1f)
                onValueChange(newValue)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(value.coerceIn(0f, 1f)).background(Color.White.copy(alpha = 0.85f)))
            Icon(imageVector = icon, contentDescription = null, tint = if (value > 0.2f) Color.Black else Color.White, modifier = Modifier.padding(bottom = 16.dp).size(22.dp))
        }
    }
}
