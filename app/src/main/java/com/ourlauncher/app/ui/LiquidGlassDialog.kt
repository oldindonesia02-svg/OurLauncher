package com.ourlauncher.app.ui

import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LiquidGlassCardDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit = onDismiss,
    confirmText: String = "Okay",
    cancelText: String = "Cancel",
    content: @Composable ColumnScope.() -> Unit
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = 380f)
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = (0.45f * animProgress.value).coerceIn(0f, 0.45f)))
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
                    .graphicsLayer {
                        scaleX = 0.85f + (0.15f * animProgress.value)
                        scaleY = 0.85f + (0.15f * animProgress.value)
                        alpha = animProgress.value
                    }
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E2836).copy(alpha = 0.88f),
                                Color(0xFF101722).copy(alpha = 0.94f)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.08f)
                            )
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    content()

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.15f),
                                            Color.White.copy(alpha = 0.05f)
                                        )
                                    )
                                )
                                .border(0.8.dp, Color.White.copy(alpha = 0.18f), CircleShape)
                                .clickable { onCancel() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cancelText,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            Color(0xFF00A2FF),
                                            Color(0xFF007AFF)
                                        )
                                    )
                                )
                                .clickable { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = confirmText,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
