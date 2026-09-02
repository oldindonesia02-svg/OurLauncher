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
import com.ourlauncher.app.ui.components.liquidGlassEffect
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
    var brightness by remember { mutableFloatStateOf(0.75f) }
    
    // Volume State Initialization
    var volume by remember { 
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) current / max else 0f) 
    }

    // Date & Time formating
    val currentDate = remember { SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(Date()) }
    val currentTime = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date()) }

    // Safe Intent Launcher
    fun launchSetting(action: String) {
        try {
            val intent = Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Toggle Mute Logic
    fun toggleMute() {
        try {
            if (isMuted) {
                audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                isMuted = false
            } else {
                audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE // Using Vibrate to avoid DND permission crash
                isMuted = true
            }
        } catch (e: Exception) {
            launchSetting(Settings.ACTION_SOUND_SETTINGS) // Fallback if permission denied
        }
    }

    AnimatedVisibility(
        visible = isOpen,
        enter = slideInVertically(initialOffsetY = { -it }, animationSpec = tween(350, easing = { fraction -> fraction })) + fadeIn(tween(250)),
        exit = slideOutVertically(targetOffsetY = { -it }, animationSpec = tween(300)) + fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)) // Subtle dim background
                .clickable(onClick = onDismiss)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -15) { // Swipe UP to dismiss
                            onDismiss()
                        }
                    }
                }
                .statusBarsPadding()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = false) {} 
                    .padding(vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                // Top Pull Indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 42.dp, height = 5.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.5f))
                )

                // ==========================================
                // ROW 1: Connectivity (2x2) & Date/Time Block
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth().height(170.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Connectivity Block (Left)
                    LiquidGlassSurface(
                        settings = settings, cornerRadius = 32.dp, isDarkTheme = isDarkTheme, 
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(14.dp), 
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
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

                    // Date & Time Block (Right)
                    LiquidGlassSurface(
                        settings = settings, cornerRadius = 32.dp, isDarkTheme = isDarkTheme, 
                        modifier = Modifier.weight(1f).fillMaxHeight()
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color.White.copy(0.7f), modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(currentTime, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(currentDate, color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                        }
                    }
                }

                // ==========================================
                // ROW 2: Extra Toggles & Vertical Sliders
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth().height(180.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left Column (Hotspot, Mute, Flashlight)
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(), 
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // Mute/Unmute
                            LiquidGlassSurface(
                                settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme, 
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { toggleMute() }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(if(isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null, tint = if(isMuted) Color(0xFFFF3B30) else Color.White)
                                }
                            }
                            // Flashlight
                            LiquidGlassSurface(
                                settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme, 
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                onClick = { flashlightOn = !flashlightOn }
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.FlashlightOn, null, tint = if (flashlightOn) Color(0xFF007AFF) else Color.White)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Hotspot (Wide Button)
                        LiquidGlassSurface(
                            settings = settings, cornerRadius = 24.dp, isDarkTheme = isDarkTheme, 
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            onClick = { 
                                // Hotspot/Tethering Settings fallback
                                try {
                                    val intent = Intent().setClassName("com.android.settings", "com.android.settings.TetherSettings")
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    launchSetting(Settings.ACTION_WIRELESS_SETTINGS)
                                }
                            }
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                Icon(Icons.Default.CellTower, null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hotspot", color = Color.White, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    // Right Column (Tall Vertical Sliders for Brightness & Volume)
                    Row(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Brightness Slider
                        LiquidGlassSlider(
                            value = brightness, 
                            onValueChange = { brightness = it }, // Needs WRITE_SETTINGS perm for real system change
                            icon = Icons.Default.WbSunny,
                            settings = settings, isDarkTheme = isDarkTheme, 
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                        // Volume Slider (Real System Volume)
                        LiquidGlassSlider(
                            value = volume, 
                            onValueChange = { 
                                volume = it
                                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * max).toInt(), 0)
                            }, 
                            icon = Icons.Default.VolumeUp, 
                            settings = settings, isDarkTheme = isDarkTheme, 
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// Helper Components for Control Center
// -------------------------------------------------------------

@Composable
fun ControlIconButton(icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp) // Large circular buttons like screenshot
            .clip(CircleShape)
            .background(if (active) Color(0xFF007AFF) else Color.White.copy(alpha = 0.15f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = Color.White, 
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun LiquidGlassSlider(
    value: Float, 
    onValueChange: (Float) -> Unit, 
    icon: ImageVector,
    settings: SettingsManager, 
    isDarkTheme: Boolean, 
    modifier: Modifier = Modifier
) {
    LiquidGlassSurface(
        settings = settings, 
        cornerRadius = 32.dp, // Pill shape exactly like 27474.png
        isDarkTheme = isDarkTheme,
        modifier = modifier.pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                change.consume()
                // Y-axis drag (up is negative, down is positive)
                val delta = -dragAmount / size.height
                val newValue = (value + delta).coerceIn(0f, 1f)
                onValueChange(newValue)
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            // The filled level background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(Color.White.copy(alpha = 0.9f)) // Bright fill
            )
            // Icon at bottom
            Icon(
                imageVector = icon, 
                contentDescription = null, 
                tint = if (value > 0.15f) Color.DarkGray else Color.White, 
                modifier = Modifier.padding(bottom = 20.dp).size(24.dp)
            )
        }
    }
}
