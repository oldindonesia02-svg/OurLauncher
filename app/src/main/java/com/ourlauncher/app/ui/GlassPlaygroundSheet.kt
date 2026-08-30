package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

@Composable
fun GlassPlaygroundSheet(
    settingsManager: SettingsManager,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    var cornerRadius by remember { mutableFloatStateOf(28f) }
    var blurRadius by remember { mutableFloatStateOf(45f) }
    var refractionHeight by remember { mutableFloatStateOf(65f) }
    var refractionAmount by remember { mutableFloatStateOf(40f) }
    var chromaticAberration by remember { mutableStateOf(true) }

    var glareOffset by remember { mutableStateOf(Offset(140f, 90f)) }

    val textColor = Color(0xFF102844)
    val accentBlue = Color(0xFF007AFF)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFFF0F7FD).copy(alpha = 0.95f),
                            Color(0xFFD6E9F8).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White, Color.White.copy(alpha = 0.3f))
                    ),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(horizontal = 22.dp, vertical = 18.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Drag Indicator Bar
            Box(
                modifier = Modifier
                    .width(36.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E3A5F).copy(alpha = 0.2f))
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Header Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Glass Playground",
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Close",
                    color = accentBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onDismiss() }
                        .padding(6.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Interactive Live Glass Surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF00C6FF), Color(0xFF0072FF), Color(0xFF4A00E0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp, 120.dp)
                        .shadow(
                            elevation = (blurRadius / 5f).dp,
                            shape = RoundedCornerShape(cornerRadius.dp),
                            ambientColor = Color.Black.copy(alpha = 0.3f)
                        )
                        .clip(RoundedCornerShape(cornerRadius.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = (blurRadius / 100f).coerceIn(0.1f, 0.85f)),
                                    Color(0xFF1E3844).copy(alpha = (refractionAmount / 100f).coerceIn(0.2f, 0.75f))
                                )
                            )
                        )
                        .border(
                            width = (refractionHeight / 40f).coerceIn(0.8f, 2.5f).dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White.copy(alpha = 0.9f),
                                    if (chromaticAberration) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0.15f)
                                )
                            ),
                            shape = RoundedCornerShape(cornerRadius.dp)
                        )
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                glareOffset = Offset(
                                    (glareOffset.x + dragAmount.x).coerceIn(20f, size.width.toFloat() - 20f),
                                    (glareOffset.y + dragAmount.y).coerceIn(20f, size.height.toFloat() - 20f)
                                )
                            }
                        }
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.85f),
                                    Color.White.copy(alpha = 0.2f),
                                    Color.Transparent
                                ),
                                center = glareOffset,
                                radius = (refractionHeight * 1.2f).dp.toPx()
                            ),
                            radius = (refractionHeight * 1.2f).dp.toPx(),
                            center = glareOffset
                        )

                        if (chromaticAberration) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFF00E5FF).copy(alpha = 0.45f),
                                        Color.Transparent
                                    ),
                                    center = Offset(size.width - 25.dp.toPx(), size.height - 25.dp.toPx()),
                                    radius = 35.dp.toPx()
                                ),
                                radius = 35.dp.toPx(),
                                center = Offset(size.width - 25.dp.toPx(), size.height - 25.dp.toPx())
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Control Sliders
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Corner radius", color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Text("${cornerRadius.toInt()} dp", color = accentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                LiquidGlassSlider(
                    value = cornerRadius,
                    onValueChange = { cornerRadius = it },
                    valueRange = 8f..48f
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Blur radius", color = textColor, fontSize = 14.sp)
                    Text("${blurRadius.toInt()}%", color = accentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                LiquidGlassSlider(
                    value = blurRadius,
                    onValueChange = { blurRadius = it },
                    valueRange = 10f..90f
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Refraction height", color = textColor, fontSize = 14.sp)
                    Text("${refractionHeight.toInt()}", color = accentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                LiquidGlassSlider(
                    value = refractionHeight,
                    onValueChange = { refractionHeight = it },
                    valueRange = 10f..100f
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Refraction amount", color = textColor, fontSize = 14.sp)
                    Text("${refractionAmount.toInt()}", color = accentBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                LiquidGlassSlider(
                    value = refractionAmount,
                    onValueChange = { refractionAmount = it },
                    valueRange = 5f..80f
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Chromatic aberration", color = textColor, fontSize = 14.sp)
                    Switch(
                        checked = chromaticAberration,
                        onCheckedChange = { chromaticAberration = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = accentBlue,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFC0D3E5)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Reset & Apply Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2ECF5))
                        .border(1.dp, Color.White, CircleShape)
                        .clickable {
                            cornerRadius = 28f
                            blurRadius = 45f
                            refractionHeight = 65f
                            refractionAmount = 40f
                            chromaticAberration = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reset",
                        color = Color(0xFF152A42),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .height(46.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00A2FF), Color(0xFF0072FF))
                            )
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Apply Glass",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

