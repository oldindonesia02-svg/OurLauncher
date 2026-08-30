package com.ourlauncher.app.ui

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
    LiquidGlassSlider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier,
        steps = steps
    )
}

@Composable
fun LiquidGlassPillSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    steps: Int = 0
) {
    LiquidGlassSlider(
        value = value,
        onValueChange = onValueChange,
        valueRange = valueRange,
        modifier = modifier,
        steps = steps
    )
}

@Composable
fun LiquidGlassSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    height: Dp = 50.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableFloatStateOf(0f) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    val thumbWidth = 34.dp
    val thumbHeight = 13.dp
    val horizontalPadding = 16.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .shadow(elevation = 8.dp, shape = CircleShape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(CircleShape)
            .background(Color(0xFF000000))
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.04f))
                ),
                shape = CircleShape
            )
            .pointerInput(valueRange, steps) {
                fun updatePosition(touchX: Float) {
                    val padPx = horizontalPadding.toPx()
                    val thumbWPx = thumbWidth.toPx()
                    val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                    val relativeX = (touchX - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                    val newFraction = relativeX / usableWidth
                    val rawVal = min + newFraction * (max - min)
                    val finalVal = if (steps > 0) {
                        val stepSize = (max - min) / (steps + 1)
                        ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                    } else rawVal
                    currentOnValueChange(finalVal.coerceIn(min, max))
                }

                detectTapGestures { offset -> updatePosition(offset.x) }
            }
            .pointerInput(valueRange, steps) {
                fun updatePosition(touchX: Float) {
                    val padPx = horizontalPadding.toPx()
                    val thumbWPx = thumbWidth.toPx()
                    val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                    val relativeX = (touchX - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                    val newFraction = relativeX / usableWidth
                    val rawVal = min + newFraction * (max - min)
                    val finalVal = if (steps > 0) {
                        val stepSize = (max - min) / (steps + 1)
                        ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                    } else rawVal
                    currentOnValueChange(finalVal.coerceIn(min, max))
                }

                detectHorizontalDragGestures { change, _ ->
                    updatePosition(change.position.x)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPadding * 2) - thumbWidth).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPadding + (usableWidthDp.value * fraction).dp
        val activeTrackWidthDp = (usableWidthDp.value * fraction).dp + (thumbWidth.value / 2f).dp

        // ১. ইনঅ্যাক্টিভ ডার্ক লাইন ট্র্যাক
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .height(3.5.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C2C2E))
        )

        // ২. অ্যাক্টিভ নিয়ন সাইয়ান-ব্লু লাইন ট্র্যাক
        if (activeTrackWidthDp > 0.dp) {
            Box(
                modifier = Modifier
                    .padding(start = horizontalPadding)
                    .width(activeTrackWidthDp)
                    .height(3.5.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF00E5FF),
                                Color(0xFF0A84FF)
                            )
                        )
                    )
            )
        }

        // ৩. গ্লাস স্পেকুলার রিফ্লেকশন
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ৪. ফ্লোটিং হোয়াইট পিল থাম্ব
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp)
                .size(width = thumbWidth, height = thumbHeight)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
