package com.ourlauncher.app.ui.controlcenter

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ControlType(val title: String, val icon: ImageVector) {
    WIFI("Wi-Fi", Icons.Default.Wifi),
    DATA("Mobile Data", Icons.Default.SignalCellularAlt),
    BLUETOOTH("Bluetooth", Icons.Default.Bluetooth),
    AIRPLANE("Airplane Mode", Icons.Default.AirplanemodeActive),
    TORCH("Flashlight", Icons.Default.FlashlightOn),
    MUTE("Mute / Vibrate", Icons.Default.VolumeOff),
    HOTSPOT("Hotspot", Icons.Default.CellTower),
    ROTATE("Auto Rotate", Icons.Default.ScreenRotation),
    DARK_MODE("Dark Mode", Icons.Default.DarkMode),
    CAMERA("Camera", Icons.Default.CameraAlt),
    CALCULATOR("Calculator", Icons.Default.Calculate),
    BATTERY("Battery Saver", Icons.Default.BatteryChargingFull),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun ControlCenterOverlay(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    settings: SettingsManager? = null
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("control_center_custom_prefs", Context.MODE_PRIVATE) }
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val cameraManager = remember { context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager }
    val cameraId = remember {
        try { cameraManager?.cameraIdList?.firstOrNull() } catch (e: Exception) { null }
    }

    var slot5 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot5", ControlType.TORCH.name) ?: ControlType.TORCH.name)) }
    var slot6 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot6", ControlType.MUTE.name) ?: ControlType.MUTE.name)) }
    var slot7 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot7", ControlType.HOTSPOT.name) ?: ControlType.HOTSPOT.name)) }
    var slot9 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot9", ControlType.ROTATE.name) ?: ControlType.ROTATE.name)) }
    var slot10 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot10", ControlType.DARK_MODE.name) ?: ControlType.DARK_MODE.name)) }
    var slot11 by remember { mutableStateOf(ControlType.valueOf(prefs.getString("slot11", ControlType.CAMERA.name) ?: ControlType.CAMERA.name)) }

    var isEditMode by remember { mutableStateOf(false) }
    var activeEditingSlot by remember { mutableStateOf<String?>(null) }

    var isTorchOn by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(audioManager.ringerMode != AudioManager.RINGER_MODE_NORMAL) }
    var isBluetoothOn by remember { mutableStateOf(true) }
    var brightnessLevel by remember { mutableFloatStateOf(0.72f) }

    var currentVolume by remember {
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
        val curr = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
        mutableFloatStateOf(if (max > 0) curr / max else 0f)
    }

    val timeText = remember { SimpleDateFormat("h:mm", Locale.getDefault()).format(Date()) }
    val amPmText = remember { SimpleDateFormat("a", Locale.getDefault()).format(Date()) }
    val dateText = remember { SimpleDateFormat("EEEE, d MMMM", Locale.getDefault()).format(Date()) }

    fun executeControl(type: ControlType) {
        if (isEditMode) return
        when (type) {
            ControlType.WIFI, ControlType.DATA -> {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        context.startActivity(Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                    } else {
                        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                    }
                } catch (e: Exception) {
                    context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                }
            }
            ControlType.BLUETOOTH -> {
                isBluetoothOn = !isBluetoothOn
                try { context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.AIRPLANE -> {
                try { context.startActivity(Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.TORCH -> {
                try {
                    if (cameraId != null && cameraManager != null) {
                        val next = !isTorchOn
                        cameraManager.setTorchMode(cameraId, next)
                        isTorchOn = next
                    }
                } catch (e: Exception) { isTorchOn = !isTorchOn }
            }
            ControlType.MUTE -> {
                try {
                    if (isMuted) {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
                        isMuted = false
                    } else {
                        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
                        isMuted = true
                    }
                } catch (e: Exception) {
                    try { context.startActivity(Intent(Settings.ACTION_SOUND_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e2: Exception) {}
                }
            }
            ControlType.HOTSPOT -> {
                try {
                    context.startActivity(Intent().apply {
                        setClassName("com.android.settings", "com.android.settings.TetherSettings")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                } catch (e: Exception) {
                    try { context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e2: Exception) {}
                }
            }
            ControlType.ROTATE -> {
                try { context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.DARK_MODE -> {
                try { context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.CAMERA -> {
                try { context.startActivity(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.CALCULATOR -> {
                try {
                    val calcIntent = context.packageManager.getLaunchIntentForPackage("com.google.android.calculator")
                        ?: Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_APP_CALCULATOR)
                    context.startActivity(calcIntent.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
                } catch (e: Exception) {
                    try { context.startActivity(Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e2: Exception) {}
                }
            }
            ControlType.BATTERY -> {
                try { context.startActivity(Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
            ControlType.SETTINGS -> {
                try { context.startActivity(Intent(Settings.ACTION_SETTINGS).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK }) } catch (e: Exception) {}
            }
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
                        if (dragAmount < -20f && !isEditMode) onDismiss()
                    }
                }
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .clickable(enabled = false) {},
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Bar with Edit Mode Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(width = 42.dp, height = 4.5.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.55f))
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(CircleShape)
                            .background(if (isEditMode) Color(0xFF007AFF) else Color.White.copy(alpha = 0.15f))
                            .clickable { isEditMode = !isEditMode }
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEditMode) "Done" else "Edit",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (isEditMode) {
                    Text(
                        text = "Tap any button to change its function",
                        color = Color(0xFF00E5FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Row 1: 2x2 Network + 2x2 Clock/Media
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    PureGlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 32.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                GlassCircleToggle(icon = Icons.Default.Wifi, active = true) { executeControl(ControlType.WIFI) }
                                GlassCircleToggle(icon = Icons.Default.SignalCellularAlt, active = true) { executeControl(ControlType.DATA) }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                GlassCircleToggle(icon = Icons.Default.Bluetooth, active = isBluetoothOn) { executeControl(ControlType.BLUETOOTH) }
                                GlassCircleToggle(icon = Icons.Default.AirplanemodeActive, active = false) { executeControl(ControlType.AIRPLANE) }
                            }
                        }
                    }

                    PureGlassCard(modifier = Modifier.weight(1f).fillMaxHeight(), cornerRadius = 32.dp) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.MusicNote, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                                Text("Now Playing", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            }
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(timeText, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(amPmText, color = Color(0xFF00E5FF), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 3.dp))
                                }
                                Text(dateText, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.SkipPrevious, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                                Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(24.dp))
                                Icon(Icons.Default.SkipNext, null, tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }

                // Row 2: Video 27530 Exact Layout (Left Buttons + Right Sliders)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(310.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Left Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            EditableCircleToggle(
                                control = slot5,
                                isEditMode = isEditMode,
                                active = if (slot5 == ControlType.TORCH) isTorchOn else (slot5 == ControlType.MUTE && isMuted),
                                onEditClick = { activeEditingSlot = "slot5" },
                                onClick = { executeControl(slot5) }
                            )
                            EditableCircleToggle(
                                control = slot6,
                                isEditMode = isEditMode,
                                active = if (slot6 == ControlType.MUTE) isMuted else (slot6 == ControlType.TORCH && isTorchOn),
                                onEditClick = { activeEditingSlot = "slot6" },
                                onClick = { executeControl(slot6) }
                            )
                        }

                        // Wide Capsule (Slot 7)
                        PureGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            cornerRadius = 28.dp,
                            onClick = {
                                if (isEditMode) activeEditingSlot = "slot7"
                                else executeControl(slot7)
                            }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(slot7.icon, null, tint = if (isEditMode) Color(0xFF00E5FF) else Color.White, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(slot7.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                if (isEditMode) {
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.Default.Edit, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // Square Card Deck
                        PureGlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            cornerRadius = 28.dp,
                            onClick = { executeControl(ControlType.SETTINGS) }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(Icons.Default.Dashboard, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(20.dp))
                                    Text("Control Deck", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    GlassCircleToggle(icon = Icons.Default.CameraAlt, active = false, size = 42.dp) { executeControl(ControlType.CAMERA) }
                                    GlassCircleToggle(icon = Icons.Default.Calculate, active = false, size = 42.dp) { executeControl(ControlType.CALCULATOR) }
                                    GlassCircleToggle(icon = Icons.Default.Settings, active = false, size = 42.dp) { executeControl(ControlType.SETTINGS) }
                                }
                            }
                        }
                    }

                    // Right Column
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Sliders
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            VideoStyleSlider(
                                value = brightnessLevel,
                                onValueChange = { brightnessLevel = it },
                                icon = Icons.Default.WbSunny,
                                modifier = Modifier.weight(1f).fillMaxHeight()
                            )
                            VideoStyleSlider(
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

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            EditableCircleToggle(
                                control = slot9,
                                isEditMode = isEditMode,
                                active = false,
                                onEditClick = { activeEditingSlot = "slot9" },
                                onClick = { executeControl(slot9) }
                            )
                            EditableCircleToggle(
                                control = slot10,
                                isEditMode = isEditMode,
                                active = false,
                                onEditClick = { activeEditingSlot = "slot10" },
                                onClick = { executeControl(slot10) }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            EditableCircleToggle(
                                control = slot11,
                                isEditMode = isEditMode,
                                active = false,
                                onEditClick = { activeEditingSlot = "slot11" },
                                onClick = { executeControl(slot11) }
                            )
                        }
                    }
                }
            }

            // Customization Picker Bottom Sheet
            if (activeEditingSlot != null) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.65f))
                        .clickable { activeEditingSlot = null },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    PureGlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.55f)
                            .clickable(enabled = false) {},
                        cornerRadius = 32.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = "Select Function for this Button",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(3),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(ControlType.values()) { item ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(Color.White.copy(alpha = 0.12f))
                                            .clickable {
                                                when (activeEditingSlot) {
                                                    "slot5" -> { slot5 = item; prefs.edit().putString("slot5", item.name).apply() }
                                                    "slot6" -> { slot6 = item; prefs.edit().putString("slot6", item.name).apply() }
                                                    "slot7" -> { slot7 = item; prefs.edit().putString("slot7", item.name).apply() }
                                                    "slot9" -> { slot9 = item; prefs.edit().putString("slot9", item.name).apply() }
                                                    "slot10" -> { slot10 = item; prefs.edit().putString("slot10", item.name).apply() }
                                                    "slot11" -> { slot11 = item; prefs.edit().putString("slot11", item.name).apply() }
                                                }
                                                activeEditingSlot = null
                                            }
                                            .padding(vertical = 12.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(item.icon, null, tint = Color(0xFF00E5FF), modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(item.title, color = Color.White, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PureGlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 26.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Box(
        modifier = modifier
            .shadow(12.dp, shape, spotColor = Color(0xFF00E5FF).copy(alpha = 0.22f), ambientColor = Color.Black.copy(alpha = 0.35f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.20f),
                        Color(0xFF0F1A24).copy(alpha = 0.48f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
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
fun GlassCircleToggle(
    icon: ImageVector,
    active: Boolean,
    size: Dp = 54.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                if (active) {
                    Brush.linearGradient(listOf(Color(0xFF007AFF), Color(0xFF0A84FF)))
                } else {
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.08f)))
                }
            )
            .border(
                1.dp,
                if (active) Color(0xFF00E5FF).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.25f),
                CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = Color.White, modifier = Modifier.size(size * 0.48f))
    }
}

@Composable
fun EditableCircleToggle(
    control: ControlType,
    isEditMode: Boolean,
    active: Boolean,
    onEditClick: () -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(
                if (active) {
                    Brush.linearGradient(listOf(Color(0xFF007AFF), Color(0xFF0A84FF)))
                } else {
                    Brush.linearGradient(listOf(Color.White.copy(alpha = 0.18f), Color.White.copy(alpha = 0.08f)))
                }
            )
            .border(
                1.dp,
                if (isEditMode) Color(0xFF00E5FF) else if (active) Color(0xFF00E5FF).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.25f),
                CircleShape
            )
            .clickable {
                if (isEditMode) onEditClick() else onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(control.icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
        if (isEditMode) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00E5FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Edit, null, tint = Color.Black, modifier = Modifier.size(10.dp))
            }
        }
    }
}

@Composable
fun VideoStyleSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    Box(
        modifier = modifier
            .shadow(14.dp, shape, spotColor = Color(0xFF00E5FF).copy(alpha = 0.20f))
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.16f),
                        Color(0xFF0A1520).copy(alpha = 0.50f)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(value.coerceIn(0f, 1f))
                    .background(Color.White.copy(alpha = 0.92f))
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (value > 0.16f) Color(0xFF0F172A) else Color.White,
                modifier = Modifier.padding(bottom = 16.dp).size(22.dp)
            )
        }
    }
}
