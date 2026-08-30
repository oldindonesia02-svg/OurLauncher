package com.ourlauncher.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
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
    height: Dp = 38.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val density = LocalDensity.current
    var isDragging by remember { mutableStateOf(false) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    // Morph: 0f = Normal White Pill, 1f = Expanded Liquid Glass Bubble
    val morphProgress by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 500f),
        label = "glassMorph"
    )

    val restThumbWidth = 30.dp
    val activeThumbWidth = 46.dp
    val restThumbHeight = 14.dp
    val activeThumbHeight = 22.dp

    val currentThumbWidth = lerp(restThumbWidth, activeThumbWidth, morphProgress)
    val currentThumbHeight = lerp(restThumbHeight, activeThumbHeight, morphProgress)
    val horizontalPaddingDp = 4.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .pointerInput(valueRange, steps) {
                val padPx = with(density) { horizontalPaddingDp.toPx() }
                val thumbWPx = with(density) { restThumbWidth.toPx() }

                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        val totalW = size.width.toFloat()
                        val usableW = (totalW - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                        val relX = (offset.x - padPx - (thumbWPx / 2f)).coerceIn(0f, usableW)
                        val newFraction = relX / usableW
                        val rawVal = min + newFraction * (max - min)
                        val finalVal = if (steps > 0) {
                            val stepSize = (max - min) / (steps + 1)
                            ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                        } else rawVal
                        currentOnValueChange(finalVal.coerceIn(min, max))
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        val totalW = size.width.toFloat()
                        val usableW = (totalW - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                        val relX = (change.position.x - padPx - (thumbWPx / 2f)).coerceIn(0f, usableW)
                        val newFraction = relX / usableW
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
        val activeTrackWidthDp = (thumbOffsetDp + (currentThumbWidth.value / 2f).dp - horizontalPaddingDp).coerceAtLeast(0.dp)

        // 1. Inactive Floating Track Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPaddingDp)
                .height(3.5.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        )

        // 2. Active Vivid Blue Line
        if (activeTrackWidthDp > 0.dp) {
            Box(
                modifier = Modifier
                    .padding(start = horizontalPaddingDp)
                    .width(activeTrackWidthDp)
                    .height(3.5.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF00C6FF), Color(0xFF0072FF))
                        )
                    )
            )
        }

        // 3. Morphing Thumb (Rest: White Pill | Dragging: Liquid Glass Bubble)
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(width = currentThumbWidth, height = currentThumbHeight)
                .shadow(
                    elevation = if (morphProgress > 0.3f) 8.dp else 3.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (morphProgress > 0.05f) {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f * morphProgress),
                                Color(0xFF101B2B).copy(alpha = 0.55f * morphProgress)
                            )
                        )
                    } else {
                        Brush.verticalGradient(listOf(Color.White, Color.White))
                    }
                )
                .border(
                    width = (1.2f * morphProgress).dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f * morphProgress),
                            Color(0xFF00C6FF).copy(alpha = 0.6f * morphProgress),
                            Color.White.copy(alpha = 0.25f * morphProgress)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Liquid Core and Glare inside the Glass Bubble
            if (morphProgress > 0.1f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color(0xFF00C6FF).copy(alpha = 0.65f * morphProgress),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = 10.dp.toPx()
                        ),
                        radius = 10.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    drawCircle(
                        color = Color.White.copy(alpha = 0.7f * morphProgress),
                        radius = 1.8.dp.toPx(),
                        center = Offset(cx - 5.dp.toPx(), cy - 3.5.dp.toPx())
                    )
                }
            }
        }
    }
}
