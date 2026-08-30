package com.ourlauncher.app.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        color = Color(0xFF8E8E93),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1C1C1E))
    ) {
        content()
    }
}

@Composable
fun SettingsDivider() {
    Divider(color = Color(0xFF2C2C2E), thickness = 0.8.dp, modifier = Modifier.padding(start = 16.dp))
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, color = Color.White, fontSize = 15.sp)
            if (subtitle != null) {
                Text(subtitle, color = Color(0xFF8E8E93), fontSize = 12.sp)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF0A84FF)
            )
        )
    }
}

@Composable
fun SettingsNavRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
            Text(title, color = Color.White, fontSize = 15.sp)
            if (subtitle != null) {
                Text(subtitle, color = Color(0xFF8E8E93), fontSize = 12.sp)
            }
        }
        Text("›", color = Color(0xFF8E8E93), fontSize = 20.sp)
    }
}

@Composable
fun CurveSlider(
    label: String,
    subtitle: String,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(label, color = Color.White, fontSize = 14.sp)
                Text(subtitle, color = Color(0xFF8E8E93), fontSize = 11.sp)
            }
            Text(String.format("%.2f", value), color = Color(0xFF00E5FF), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LiquidGlassSlider(value = value, onValueChange = onValueChange, valueRange = 0f..1.5f)
    }
}

@Composable
fun BezierCanvas(x1: Float, y1: Float, x2: Float, y2: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141416))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(0f, h)
                cubicTo(
                    x1.coerceIn(0f, 1f) * w, h - (y1 * h),
                    x2.coerceIn(0f, 1f) * w, h - (y2 * h),
                    w, 0f
                )
            }
            drawPath(path, color = Color(0xFF0A84FF), style = Stroke(width = 4f))
            drawCircle(Color(0xFF34C759), radius = 6f, center = Offset(x1.coerceIn(0f, 1f) * w, h - (y1 * h)))
            drawCircle(Color(0xFFFF9500), radius = 6f, center = Offset(x2.coerceIn(0f, 1f) * w, h - (y2 * h)))
        }
    }
}

@Composable
fun PhoneMockupPreview(durationMs: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "preview")
    val p by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs.coerceAtLeast(200), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "progress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(110.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color.Black)
                .padding(6.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            val currentH = 30.dp + (140.dp * p)
            val currentW = 30.dp + (75.dp * p)
            Box(
                modifier = Modifier
                    .size(currentW, currentH)
                    .clip(RoundedCornerShape((14 * (1f - p)).dp))
                    .background(Color(0xFF0A84FF))
            )
        }
    }
}
