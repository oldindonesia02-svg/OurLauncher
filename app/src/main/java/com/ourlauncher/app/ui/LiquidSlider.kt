package com.ourlauncher.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun LiquidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    LiquidGlassSlider(value, onValueChange, valueRange, modifier, steps)
}

@Composable
fun LiquidGlassPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    LiquidGlassSlider(value, onValueChange, valueRange, modifier, steps)
}

@Composable
fun LiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    height: Dp = 48.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableFloatStateOf(0f) }
    var isInteracting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    // Morph Progress: 0f = Normal White Button, 1f = Expanded Liquid Glass Capsule Bubble
    val morphProgress by animateFloatAsState(
        targetValue = if (isInteracting) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 500f),
        label = "glassMorph"
    )

    // Dynamic Thumb Size Interpolation
    val restThumbWidth = 32.dp
    val activeThumbWidth = 46.dp
    val restThumbHeight = 14.dp
    val activeThumbHeight = 24.dp

    val currentThumbWidth = lerp(restThumbWidth, activeThumbWidth, morphProgress)
    val currentThumbHeight = lerp(restThumbHeight, activeThumbHeight, morphProgress)
    val horizontalPaddingDp = 14.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.25f))
            .clip(CircleShape)
            // Frosted Translucent Glass Capsule Background
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.12f),
                        Color.Black.copy(alpha = 0.35f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.45f),
                        Color.White.copy(alpha = 0.08f)
                    )
                ),
                shape = CircleShape
            )
            .pointerInput(valueRange, steps) {
                detectTapGestures(
                    onPress = { offset ->
                        isInteracting = true
                        val padPx = horizontalPaddingDp.toPx()
                        val thumbWPx = restThumbWidth.toPx()
                        val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                        val relX = (offset.x - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                        val newFraction = relX / usableWidth
                        val rawVal = min + newFraction * (max - min)
                        val finalVal = if (steps > 0) {
                            val stepSize = (max - min) / (steps + 1)
                            ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                        } else rawVal
                        currentOnValueChange(finalVal.coerceIn(min, max))

                        tryAwaitRelease()
                        isInteracting = false
                    }
                )
            }
            .pointerInput(valueRange, steps) {
                detectHorizontalDragGestures(
                    onDragStart = { isInteracting = true },
                    onDragEnd = { isInteracting = false },
                    onDragCancel = { isInteracting = false },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val padPx = horizontalPaddingDp.toPx()
                        val thumbWPx = restThumbWidth.toPx()
                        val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                        val relX = (change.position.x - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                        val newFraction = relX / usableWidth
                        val rawVal = min + newFraction * (max - min)
                        val finalVal = if (steps > 0) {
                            val stepSize = (max - min) / (steps + 1)
                            ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                        } else rawVal
                        currentOnValueChange(finalVal.coerceIn(min, max))
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPaddingDp * 2) - currentThumbWidth).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPaddingDp + (usableWidthDp.value * fraction).dp
        val activeTrackWidthDp = (thumbOffsetDp + (currentThumbWidth / 2f) - horizontalPaddingDp).coerceAtLeast(0.dp)

        // 1. Inactive Line Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPaddingDp)
                .height(3.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
        )

        // 2. Active Glowing Neon Cyan Track
        if (activeTrackWidthDp > 0.dp) {
            Box(
                modifier = Modifier
                    .padding(start = horizontalPaddingDp)
                    .width(activeTrackWidthDp)
                    .height(3.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00E5FF), Color(0xFF007AFF))
                        )
                    )
            )
        }

        // 3. Top Specular Glass Reflection Glare
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 18.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.45f), Color.Transparent)
                    )
                )
        )

        // 4. Morphing Liquid Glass Thumb / Normal White Button
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(width = currentThumbWidth, height = currentThumbHeight)
                .shadow(
                    elevation = if (morphProgress > 0.5f) 10.dp else 4.dp,
                    shape = CircleShape,
                    ambientColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(CircleShape)
                // Background Layer: White in rest, Translucent Glass when sliding
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 1f - (0.75f * morphProgress)),
                            Color.White.copy(alpha = 1f - (0.90f * morphProgress))
                        )
                    )
                )
                // Refractive Glass Border on Morph
                .border(
                    width = if (morphProgress > 0.1f) 1.2.dp else 0.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f * morphProgress),
                            Color(0xFF00E5FF).copy(alpha = 0.6f * morphProgress),
                            Color.White.copy(alpha = 0.25f * morphProgress)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Internal Liquid Core visible inside the glass bubble during slide
            if (morphProgress > 0.05f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Inner Liquid Droplet Glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00E5FF).copy(alpha = 0.75f * morphProgress),
                                Color(0xFF007AFF).copy(alpha = 0.35f * morphProgress),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = 12.dp.toPx()
                        ),
                        radius = 12.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    // Top Specular Bubble Glare
                    drawCircle(
                        color = Color.White.copy(alpha = 0.65f * morphProgress),
                        radius = 2.dp.toPx(),
                        center = Offset(cx - 6.dp.toPx(), cy - 4.dp.toPx())
                    )
                }
            }
        }
    }
}
