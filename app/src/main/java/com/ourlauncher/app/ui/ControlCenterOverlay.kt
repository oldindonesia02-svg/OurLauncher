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

    var flashlightOn by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) }
    var brightness by remember { mutableFloatStateOf(0.7f) }
    
    var volume by remember { 
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) current / max else 0f) 
    }

    val currentDate = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()) }

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
                // পেছনের অ্যাপ আইকন পুরোপুরি ঢেকে ফেলার জন্য ডিপ ব্যাকগ্রাউন্ড
                .background(Color.Black.copy(alpha = 0.75f))
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {},
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Drag Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 38.dp, height = 4.5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.4f))
                )

                // Row 1: Connectivity (2x2 Grid) + Clock/Date Tile
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
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
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                ControlTileIcon(Icons.Default.Wifi, active = true) {
                                    launchSetting(Settings.ACTION_WIFI_SETTINGS)
                                }
                                ControlTileIcon(Icons.Default.SignalCellularAlt, active = true) {
                                    launchSetting(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                ControlTileIcon(Icons.Default.Bluetooth, active = true) {
                                    launchSetting(Settings.ACTION_BLUETOOTH_SETTINGS)
                                }
                                ControlTileIcon(Icons.Default.AirplanemodeActive, active = false) {
                                    launchSetting(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                                }
                            }
                        }
                    }

                    // Clock / Date Glass Tile
                    LiquidGlassSurface(
                        settings = settings,
                        cornerRadius = 28.dp,
                        isDarkTheme = isDarkTheme,
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = currentTime, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = currentDate, color = Color.White.copy(alpha = 0.7f), fontSize = 13.sp)
                        }
                    }
                }

                // Row 2: Sliders & Extra Actions
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column: Toggles (Mute, Torch, Hotspot)
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Flashlight
                            LiquidGlassSurface(
                                settings = settings,
                                cornerRadius = 22.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { flashlightOn = !flashlightOn }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.FlashlightOn, contentDescription = null, tint = if (flashlightOn) Color(0xFF007AFF) else Color.White)
                                }
                            }

                            // Mute
                            LiquidGlassSurface(
                                settings = settings,
                                cornerRadius = 22.dp,
                                isDarkTheme = isDarkTheme,
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = {
                                    if (isMuted) {
                                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                        isMuted = false
                                    } else {
                                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                        isMuted = true
                                    }
                                }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, contentDescription = null, tint = if (isMuted) Color(0xFFFF3B30) else Color.White)
                                }
                            }
                        }

                        // Hotspot Tile
                        LiquidGlassSurface(
                            settings = settings,
                            cornerRadius = 22.dp,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            onClick = {
                                try {
                                    val intent = Intent().setClassName("com.android.settings", "com.android.settings.TetherSettings")
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    launchSetting(Settings.ACTION_WIRELESS_SETTINGS)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.CellTower, contentDescription = null, tint = Color.White)
                                Text("Hotspot", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Right Columns: 2 Vertical Pill Sliders
                    Row(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Brightness
                        LiquidPillSlider(
                            value = brightness,
                            onValueChange = { brightness = it },
                            icon = Icons.Default.WbSunny,
                            settings = settings,
                            isDarkTheme = isDarkTheme,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        // Volume
                        LiquidPillSlider(
                            value = volume,
                            onValueChange = {
                                volume = it
                                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * max).toInt(), 0)
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
fun ControlTileIcon(icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.12f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun LiquidPillSlider(
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
                val newValue = (value + delta).coerceIn(0f, 1f)
                onValueChange(newValue)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(Color.White.copy(alpha = 0.88f))
            )
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (value > 0.18f) Color.DarkGray else Color.White,
                modifier = Modifier.padding(bottom = 18.dp).size(22.dp)
            )
        }
    }
}
