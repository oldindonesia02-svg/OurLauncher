package com.ourlauncher.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        color = Color.White.copy(alpha = 0.45f),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 12.dp, top = 16.dp, bottom = 6.dp)
    )
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1C1E))
            .padding(4.dp),
        content = content
    )
}

@Composable
fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .height(0.5.dp)
            .background(Color.White.copy(alpha = 0.1f))
    )
}

@Composable
fun SettingsNavRow(title: String, subtitle: String? = null, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
        }
        Text(text = "›", color = Color.White.copy(alpha = 0.3f), fontSize = 20.sp)
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String? = null,
    checked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Color.White, fontSize = 16.sp)
            if (subtitle != null) {
                Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 12.sp)
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SwipeActionPicker(label: String, selected: String, onSelect: (String) -> Unit) {
    val options = listOf("none" to "None", "drawer" to "Open App Drawer", "settings" to "Open Settings")
    SettingsSectionHeader(label.uppercase())
    SettingsGroup {
        options.forEach { (value, displayName) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(value) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF0A84FF))
                )
                Text(text = displayName, color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
fun BezierCanvas(x1: Float, y1: Float, x2: Float, y2: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF141418))
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val gridColor = Color.White.copy(alpha = 0.08f)
            drawLine(gridColor, Offset(w * 0.25f, 0f), Offset(w * 0.25f, h), 1f)
            drawLine(gridColor, Offset(w * 0.5f, 0f), Offset(w * 0.5f, h), 1f)
            drawLine(gridColor, Offset(w * 0.75f, 0f), Offset(w * 0.75f, h), 1f)
            drawLine(gridColor, Offset(0f, h * 0.5f), Offset(w, h * 0.5f), 1f)

            val cp1 = Offset(w * x1.coerceIn(0f, 1f), h * (1f - y1.coerceIn(0f, 1.5f)))
            val cp2 = Offset(w * x2.coerceIn(0f, 1f), h * (1f - y2.coerceIn(0f, 1.5f)))

            drawLine(Color.Red.copy(alpha = 0.4f), Offset(0f, h), cp1, 2f)
            drawLine(Color.Green.copy(alpha = 0.4f), Offset(w, 0f), cp2, 2f)

            val path = Path().apply {
                moveTo(0f, h)
                cubicTo(cp1.x, cp1.y, cp2.x, cp2.y, w, 0f)
            }
            drawPath(path = path, color = Color(0xFF0A84FF), style = Stroke(width = 4f))
            drawCircle(color = Color(0xFFFF3B30), radius = 6f, center = cp1)
            drawCircle(color = Color(0xFF34C759), radius = 6f, center = cp2)
        }
    }
}

@Composable
fun CurveSlider(label: String, subtitle: String, valIn: Float, onChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(text = String.format("%.2f", valIn), color = Color(0xFF0A84FF), fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = subtitle, color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
        Slider(value = valIn, onValueChange = onChange, valueRange = 0f..1.5f)
    }
}
