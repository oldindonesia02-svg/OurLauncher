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
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ControlCenterOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    settings: SettingsManager
) {
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager }
    val cameraId = remember {
        try { cameraManager?.cameraIdList?.firstOrNull() } catch (e: Exception) { null }
    }

    var isTorchOn by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) }
    var brightness by remember { mutableFloatStateOf(0.72f) }

    var currentVolume by remember {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) curr / max else 0f)
    }

    val timeText = remember { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()) }
    val amPmText = remember { SimpleDateFormat("a", Locale.getDefault()).format(Date()) }
    val dateText = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    fun openNetworkPanel() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            } else {
                context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    fun openAction(action: String) {
        try {
            context.startActivity(Intent(action).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun toggleFlashlight() {
        try {
            if (cameraId != null && cameraManager != null) {
                val next = !isTorchOn
                cameraManager.setTorchMode(cameraId, next)
                isTorchOn = next
            }
        } catch (e: Exception) {
            isTorchOn = !isTorchOn
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
                .clickable(onClick = onDismiss)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount < -20f) onDismiss()
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
                // Grab Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(width = 44.dp, height = 4.5.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.55f))
                )

                // ==========================================
                // Row 1: Connectivity Card + Time Card
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth().height(165.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Connectivity Tile
                    GlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 30.dp) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                GlassCircleButton(icon = Icons.Default.Wifi, active = true) { openNetworkPanel() }
                                GlassCircleButton(icon = Icons.Default.SignalCellularAlt, active = true) { openNetworkPanel() }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                GlassCircleButton(icon = Icons.Default.Bluetooth, active = true) {
                                    openAction(Settings.ACTION_BLUETOOTH_SETTINGS)
                                }
                                GlassCircleButton(icon = Icons.Default.AirplanemodeActive, active = false) {
                                    openAction(Settings.ACTION_AIRPLANE_MODE_SETTINGS)
                                }
                            }
                        }
                    }

                    // Clock & Date Tile
                    GlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 30.dp) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(timeText, color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(amPmText, color = Color(0xFF00E5FF), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 4.dp))
                            }
                            Text(dateText, color = Color.White.copy(alpha = 0.75f), fontSize = 12.sp)
                        }
                    }
                }

                // ==========================================
                // Row 2: Toggles + Vertical Pill Sliders
                // ==========================================
                Row(
                    modifier = Modifier.fillMaxWidth().height(200.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth().weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Flashlight
                            GlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 24.dp, onClick = { toggleFlashlight() }) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.FlashlightOn, null,
                                        tint = if (isTorchOn) Color(0xFF00E5FF) else Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            // Mute Mode
                            GlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 24.dp, onClick = {
                                try {
                                    if (isMuted) {
                                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                                        isMuted = false
                                    } else {
                                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                                        isMuted = true
                                    }
                                } catch (e: Exception) {
                                    openAction(Settings.ACTION_SOUND_SETTINGS)
                                }
                            }) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, null,
                                        tint = if (isMuted) Color(0xFFFF453A) else Color.White,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }
                        }

                        // Hotspot Capsule
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().weight(0.9f),
                            cornerRadius = 24.dp,
                            onClick = {
                                try {
                                    context.startActivity(Intent().apply {
                                        setClassName("com.android.settings", "com.android.settings.TetherSettings")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    })
                                } catch (e: Exception) {
                                    openAction(Settings.ACTION_WIRELESS_SETTINGS)
                                }
                            }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.CellTower, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Hotspot", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Settings Capsule
                        GlassCard(
                            modifier = Modifier.fillMaxWidth().weight(0.9f),
                            cornerRadius = 24.dp,
                            onClick = { openAction(Settings.ACTION_SETTINGS) }
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Settings", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Right Column: Vertical Pill Sliders
                    Row(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        LiquidGlassPillSlider(
                            value = brightness,
                            onValueChange = { brightness = it },
                            icon = Icons.Default.WbSunny,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )

                        LiquidGlassPillSlider(
                            value = currentVolume,
                            onValueChange = {
                                currentVolume = it
                                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (it * max).toInt().coerceIn(0, max), 0)
                            },
                            icon = Icons.Default.VolumeUp,
                            modifier = Modifier.weight(1f).fillMaxHeight()
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// খাঁটি ক্রিস্টাল লিকুইড গ্লাস সারফেস (ভেতরের কনটেন্ট ১০০% শার্প থাকবে)
// -------------------------------------------------------------
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 26.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(16.dp, shape, spotColor = Color(0xFF00E5FF).copy(alpha = 0.25f), ambientColor = Color.Black.copy(alpha = 0.35f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color(0xFF0F1A24).copy(alpha = 0.55f)
                    )
                )
            )
            .border(
                width = 1.3.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color(0xFF00E5FF).copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape = shape
            )
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

@Composable
fun GlassCircleButton(
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
                    Brush.linearGradient(listOf(Color(0xFF00B4D8), Color(0xFF0077B6)))
                } else {
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.20f), Color.White.copy(alpha = 0.08f)))
                }
            )
            .border(
                1.dp,
                if (active) Color(0xFF00E5FF).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.25f),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun LiquidGlassPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .shadow(16.dp, shape, spotColor = Color(0xFF00E5FF).copy(alpha = 0.25f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.18f),
                        Color(0xFF0A1520).copy(alpha = 0.55f)
                    )
                )
            )
            .border(
                1.3.dp,
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color(0xFF00E5FF).copy(alpha = 0.35f),
                        Color.White.copy(alpha = 0.12f)
                    )
                ),
                shape
            )
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    val delta = -dragAmount / size.height
                    onValueChange((value + delta).coerceIn(0f, 1f))
                }
            }
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            // White Level Fill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(Color.White.copy(alpha = 0.92f))
            )

            // Icon Indicator
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (value > 0.16f) Color(0xFF0F172A) else Color.White,
                modifier = Modifier.padding(bottom = 18.dp).size(24.dp)
            )
        }
    }
}
