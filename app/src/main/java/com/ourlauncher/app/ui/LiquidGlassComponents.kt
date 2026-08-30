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
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
            // Ultra-Translucent Liquid Glass Layer
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
        // Specular Top Refraction Highlight
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

    // Outer container has ZERO black tint (100% Transparent Pass-through)
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
