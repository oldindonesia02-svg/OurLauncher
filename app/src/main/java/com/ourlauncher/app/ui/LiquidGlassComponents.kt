package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Liquid Glass Action Button
@Composable
fun LiquidGlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true
) {
    val bg = if (isPrimary) {
        Brush.horizontalGradient(
            listOf(
                Color(0xFF00B4DB).copy(alpha = 0.85f),
                Color(0xFF0083B0).copy(alpha = 0.90f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.20f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    val borderBrush = if (isPrimary) {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.70f),
                Color(0xFF00E5FF).copy(alpha = 0.40f)
            )
        )
    } else {
        Brush.verticalGradient(
            listOf(
                Color.White.copy(alpha = 0.45f),
                Color.White.copy(alpha = 0.12f)
            )
        )
    }

    Box(
        modifier = modifier
            .height(48.dp)
            .clip(CircleShape)
            .background(bg)
            .border(1.2.dp, borderBrush, CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// 2. Pure Liquid Crystal Glass Container
@Composable
fun LiquidGlassContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = Color(0xFF00E5FF).copy(alpha = 0.25f),
                ambientColor = Color.Black.copy(alpha = 0.20f)
            )
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color(0xFF00E5FF).copy(alpha = 0.08f),
                        Color(0xFF0A1926).copy(alpha = 0.35f)
                    )
                )
            )
            .border(
                width = 1.4.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.85f),
                        Color(0xFF00E5FF).copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.20f)
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.8.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.90f),
                            Color(0xFF00E5FF).copy(alpha = 0.70f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ) {
            content()
        }
    }
}

// 3. Completely Transparent Backdrop Liquid Dialog
@Composable
fun LiquidGlassDialog(
    title: String,
    message: String = "",
    confirmText: String = "Okay",
    cancelText: String = "Cancel",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onCancel: () -> Unit = onDismiss,
    content: (@Composable ColumnScope.() -> Unit)? = null
) {
    BackHandler { onDismiss() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() }
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        ) {
            LiquidGlassContainer(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = message,
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                if (content != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    content()
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    LiquidGlassButton(
                        text = cancelText,
                        onClick = onCancel,
                        isPrimary = false,
                        modifier = Modifier.weight(1f)
                    )
                    LiquidGlassButton(
                        text = confirmText,
                        onClick = onConfirm,
                        isPrimary = true,
                        modifier = Modifier.weight(1.2f)
                    )
                }
            }
        }
    }
}

// 4. Liquid Glass Toggle Row
@Composable
fun LiquidGlassToggle(
    title: String = "",
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (title.isNotBlank()) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF00E5FF),
                uncheckedThumbColor = Color.White.copy(alpha = 0.8f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f),
                uncheckedBorderColor = Color.White.copy(alpha = 0.3f)
            )
        )
    }
}

// 5. Liquid Glass Slider
@Composable
fun LiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier.fillMaxWidth(),
        colors = SliderDefaults.colors(
            thumbColor = Color.White,
            activeTrackColor = Color(0xFF00E5FF),
            inactiveTrackColor = Color.White.copy(alpha = 0.2f)
        )
    )
}

// 6. Liquid Drawer Search Bar
@Composable
fun LiquidDrawerSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search apps..."
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .shadow(6.dp, CircleShape)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.22f),
                        Color(0xFF0F2027).copy(alpha = 0.40f)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.65f), Color(0xFF00E5FF).copy(alpha = 0.30f))
                ),
                CircleShape
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search",
                tint = Color.White.copy(alpha = 0.75f),
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 15.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(Color(0xFF00E5FF)),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
