package com.ourlauncher.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.layout.onSizeChanged
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
    height: Dp = 40.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    // Morph State: 0f = Normal White Button, 1f = Liquid Glass Capsule Toggle
    val morphProgress by animateFloatAsState(
        targetValue = if (isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 500f),
        label = "glassMorph"
    )

    val restThumbWidth = 30.dp
    val activeThumbWidth = 48.dp
    val restThumbHeight = 14.dp
    val activeThumbHeight = 24.dp

    val currentThumbWidth = lerp(restThumbWidth, activeThumbWidth, morphProgress)
    val currentThumbHeight = lerp(restThumbHeight, activeThumbHeight, morphProgress)
    val horizontalPaddingDp = 6.dp

    fun updateValue(touchX: Float) {
        val padPx = 6.dp.value
        val thumbWPx = restThumbWidth.value
        val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
        val relX = (touchX - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
        val newFraction = relX / usableWidth
        val rawVal = min + newFraction * (max - min)
        val finalVal = if (steps > 0) {
            val stepSize = (max - min) / (steps + 1)
            ((rawVal - min) / stepSize).roundToInt() * stepSize + min
        } else rawVal
        currentOnValueChange(finalVal.coerceIn(min, max))
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .pointerInput(valueRange, steps) {
                detectTapGestures(
                    onPress = { offset ->
                        isDragging = true
                        updateValue(offset.x)
                        tryAwaitRelease()
                        isDragging = false
                    }
                )
            }
            .pointerInput(valueRange, steps) {
                detectDragGestures(
                    onDragStart = { offset ->
                        isDragging = true
                        updateValue(offset.x)
                    },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false },
                    onDrag = { change, _ ->
                        change.consume()
                        updateValue(change.position.x)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPaddingDp * 2) - currentThumbWidth).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPaddingDp + (usableWidthDp.value * fraction).dp
        val activeTrackWidthDp = (thumbOffsetDp + (currentThumbWidth / 2f) - horizontalPaddingDp).coerceAtLeast(0.dp)

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

        // 3. Morphing Thumb: Rest = White Pill, Dragging = Liquid Glass Bubble
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(width = currentThumbWidth, height = currentThumbHeight)
                .shadow(
                    elevation = if (morphProgress > 0.3f) 8.dp else 3.dp,
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.3f)
                )
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (morphProgress > 0.05f) {
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.25f * morphProgress),
                                Color(0xFF101B2B).copy(alpha = 0.45f * morphProgress)
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
                            Color.White.copy(alpha = 0.75f * morphProgress),
                            Color(0xFF00C6FF).copy(alpha = 0.5f * morphProgress),
                            Color.White.copy(alpha = 0.2f * morphProgress)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Liquid Droplet & Lens Glare inside the Glass Bubble
            if (morphProgress > 0.1f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2f
                    val cy = size.height / 2f

                    // Blue Liquid Core
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

                    // Top Glass Glare
                    drawCircle(
                        color = Color.White.copy(alpha = 0.6f * morphProgress),
                        radius = 1.8.dp.toPx(),
                        center = Offset(cx - 5.dp.toPx(), cy - 3.5.dp.toPx())
                    )
                }
            }
        }
    }
}
