package com.ourlauncher.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
    height: Dp = 36.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val density = LocalDensity.current
    var widthPx by remember { mutableFloatStateOf(0f) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    val thumbWidthDp = 30.dp
    val thumbHeightDp = 14.dp
    val horizontalPaddingDp = 4.dp

    fun updatePosition(touchXPx: Float) {
        if (widthPx <= 0f) return
        val padPx = with(density) { horizontalPaddingDp.toPx() }
        val thumbWPx = with(density) { thumbWidthDp.toPx() }
        val usableWidthPx = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
        val relativeX = (touchXPx - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidthPx)
        val newFraction = relativeX / usableWidthPx
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
                detectTapGestures { offset ->
                    updatePosition(offset.x)
                }
            }
            .pointerInput(valueRange, steps) {
                detectHorizontalDragGestures(
                    onDragStart = { offset -> updatePosition(offset.x) },
                    onHorizontalDrag = { change, _ ->
                        change.consume()
                        updatePosition(change.position.x)
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPaddingDp * 2) - thumbWidthDp).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPaddingDp + (usableWidthDp.value * fraction).dp
        val activeTrackWidthDp = (thumbOffsetDp + (thumbWidthDp.value / 2f).dp - horizontalPaddingDp).coerceAtLeast(0.dp)

        // 1. Inactive Track Line
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPaddingDp)
                .height(3.5.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.22f))
        )

        // 2. Active Glowing Blue Line
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

        // 3. Normal White Pill Thumb
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(width = thumbWidthDp, height = thumbHeightDp)
                .shadow(elevation = 3.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
