package com.ourlauncher.app.ui.controlcenter

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import com.ourlauncher.app.ui.components.LiquidGlassSurface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ControlCenterOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    settings: SettingsManager,
    isDarkTheme: Boolean = true
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager }
    val cameraId = remember {
        try { cameraManager?.cameraIdList?.firstOrNull() } catch (e: Exception) { null }
    }

    var isFlashlightOn by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) }
    var isBluetoothActive by remember { mutableStateOf(true) }
    var brightnessLevel by remember { mutableFloatStateOf(0.72f) }

    var currentVolume by remember {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) curr / max else 0f)
    }

    val currentTime = remember { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()) }
    val currentAmPm = remember { SimpleDateFormat("a", Locale.getDefault()).format(Date()) }
    val currentDate = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    fun openInternetPanel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val panelIntent = Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(panelIntent)
            } else {
                val fallbackIntent = Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(fallbackIntent)
            }
        } catch (e: Exception) {
            val fallback = Intent(Settings.ACTION_WIRELESS_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(fallback)
        }
    }

    fun openSetting(action: String) {
        try {
            val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleTorch() {
        try {
            if (cameraId != null && cameraManager != null) {
                val nextState = !isFlashlightOn
                cameraManager.setTorchMode(cameraId, nextState)
                isFlashlightOn = nextState
            }
        } catch (e: Exception) {
            e.printStackTrace()
            isFlashlightOn = !isFlashlightOn
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(320)) + fadeIn(tween(250)),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(280)) + fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                // কোনো কালো পর্দা নেই, একদম স্বচ্ছ ওভারলে যাতে পেছনের ব্লার ওয়ালপেপার ফুটে ওঠে
                .background(Color.Black.copy(alpha = 0.12f))
                .clickable(onClick = onDismiss)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20f) {
                            onDismiss()
                        }
                    }
                }
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {},
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Top Grab Bar
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 42.dp, height = 4.5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.5f))
                )

                // ==========================================
                // ROW 1: 2x2 Network Tile + Clock Card
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Connectivity Tile
                    LiquidGlassSurface(
                        settings = settings,
                        cornerRadius = 28.dp,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                QuickActionCircle(icon = Icons.Default.Wifi, active = true) {
                                    openInternetPanel()
                                }
                                QuickActionCircle(icon = Icons.Default.SignalCellularAlt, active = true) {
                                    openInternetPanel()
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                QuickActionCircle(icon = Icons.Default.Bluetooth, active = isBluetoothActive) {
                                    isBluetoothActive = !isBluetoothActive
                                    openSetting(Settings.ACTION_BLUETOOTH_SETTINGS)
                                }
                                QuickActionCircle(icon = Icons.Default.AirplanemodeActive, active = false) {
                                    openSetting(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                                }
                            }
                        }
                    }

                    // Date & Time Display Tile
                    LiquidGlassSurface(
                        settings = settings,
                        cornerRadius = 28.dp,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = currentTime,
                                    color = Color.White,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentAmPm,
                                    color = Color(0xFF00E5FF),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = currentDate,
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }

                // ==========================================
                // ROW 2: Toggles + Sliders
                // ==========================================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(195.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Flashlight Tile
                            LiquidGlassSurface(
                                settings = settings,
                                cornerRadius = 22.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { toggleTorch() }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.FlashlightOn,
                                        contentDescription = "Flashlight",
                                        tint = if (isFlashlightOn) Color(0xFF00E5FF) else Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            // Sound Mode (Mute) Tile
                            LiquidGlassSurface(
                                settings = settings,
                                cornerRadius = 22.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = {
                                    try {
                                        if (isMuted) {
                                            audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                            isMuted = false
                                        } else {
                                            audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                            isMuted = true
                                        }
                                    } catch (e: Exception) {
                                        openSetting(Settings.ACTION_SOUND_SETTINGS)
                                    }
                                }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = "Sound Mode",
                                        tint = if (isMuted) Color(0xFFFF453A) else Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        // Hotspot Capsule Tile
                        LiquidGlassSurface(
                            settings = settings,
                            cornerRadius = 22.dp,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.9f),
                            onClick = {
                                try {
                                    val tetherIntent = Intent().apply {
                                        setClassName("com.android.settings", "com.android.settings.TetherSettings")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                    context.startActivity(tetherIntent)
                                } catch (e: Exception) {
                                    openSetting(Settings.ACTION_WIRELESS_SETTINGS)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CellTower,
                                    contentDescription = "Hotspot",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Hotspot",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Settings Shortcut Tile
                        LiquidGlassSurface(
                            settings = settings,
                            cornerRadius = 22.dp,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.9f),
                            onClick = { openSetting(Settings.ACTION_SETTINGS) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Right Column: 2 Tall Pill Sliders
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Brightness Slider
                        BackdropPillSlider(
                            value = brightnessLevel,
                            onValueChange = { brightnessLevel = it },
                            icon = Icons.Default.WbSunny,
                            settings = settings,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        // Volume Slider
                        BackdropPillSlider(
                            value = currentVolume,
                            onValueChange = {
                                currentVolume = it
                                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(
                                    AudioManager.STREAM_MUSIC,
                                    (it * max).toInt().coerceIn(0, max),
                                    0
                                )
                            },
                            icon = Icons.Default.VolumeUp,
                            settings = settings,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickActionCircle(
    icon: ImageVector,
    active: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    Brush.linearGradient(listOf(Color(0xFF0091EA), Color(0xFF00B4D8)))
                } else {
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.08f)))
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun BackdropPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    settings: SettingsManager,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        settings = settings,
        cornerRadius = 32.dp,
        isDarkTheme = isDarkTheme,
        modifier = modifier.pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                change.consume()
                val delta = -dragAmount / size.height
                val nextValue = (value + delta).coerceIn(0f, 1f)
                onValueChange(nextValue)
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(Color.White.copy(alpha = 0.90f))
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (value > 0.16f) Color(0xFF1E293B) else Color.White,
                modifier = Modifier
                    .padding(bottom = 18.dp)
                    .size(24.dp)
            )
        }
    }
}
