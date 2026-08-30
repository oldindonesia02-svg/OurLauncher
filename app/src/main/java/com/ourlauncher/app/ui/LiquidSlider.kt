package com.ourlauncher.app.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    LiquidGlassPillSlider(
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
    steps: Int = 0
) {
    LiquidGlassPillSlider(
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
    var widthPx by remember { mutableFloatStateOf(1f) }
    val thumbWidthDp: Dp = 42.dp
    val sliderHeightDp: Dp = 46.dp

    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = spring(stiffness = 800f, dampingRatio = 0.8f),
        label = "pillThumb"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(sliderHeightDp)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .clip(RoundedCornerShape(sliderHeightDp / 2))
            .background(Color.Black.copy(alpha = 0.65f))
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White.copy(alpha = 0.25f), Color.White.copy(alpha = 0.05f))
                ),
                shape = RoundedCornerShape(sliderHeightDp / 2)
            )
            .pointerInput(valueRange, steps) {
                fun updateFromPosition(xPos: Float) {
                    val rawFrac = (xPos / widthPx).coerceIn(0f, 1f)
                    val rawVal = valueRange.start + rawFrac * (valueRange.endInclusive - valueRange.start)
                    val finalVal = if (steps > 0) {
                        val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                        ((rawVal - valueRange.start) / stepSize).roundToInt() * stepSize + valueRange.start
                    } else rawVal
                    onValueChange(finalVal.coerceIn(valueRange.start, valueRange.endInclusive))
                }

                detectTapGestures { offset -> updateFromPosition(offset.x) }
            }
            .pointerInput(valueRange, steps) {
                fun updateFromPosition(xPos: Float) {
                    val rawFrac = (xPos / widthPx).coerceIn(0f, 1f)
                    val rawVal = valueRange.start + rawFrac * (valueRange.endInclusive - valueRange.start)
                    val finalVal = if (steps > 0) {
                        val stepSize = (valueRange.endInclusive - valueRange.start) / (steps + 1)
                        ((rawVal - valueRange.start) / stepSize).roundToInt() * stepSize + valueRange.start
                    } else rawVal
                    onValueChange(finalVal.coerceIn(valueRange.start, valueRange.endInclusive))
                }

                detectHorizontalDragGestures { change, _ ->
                    updateFromPosition(change.position.x)
                }
            }
    ) {
        val maxOffsetDp = maxWidth - thumbWidthDp - 8.dp
        val currentOffsetDp = (maxOffsetDp.value * animatedFraction).dp + 4.dp
        val activeWidthDp = (maxWidth.value * animatedFraction).dp.coerceAtLeast(sliderHeightDp)

        // ১. অ্যাক্টিভ লিকুইড সাইয়ান ফিল
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(activeWidthDp)
                .clip(RoundedCornerShape(sliderHeightDp / 2))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF00A2FF).copy(alpha = 0.35f),
                            Color(0xFF00E5FF).copy(alpha = 0.75f)
                        )
                    )
                )
        )

        // ২. গ্লাস স্পেকুলার লাইট গ্লেয়ার
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    )
                )
        )

        // ৩. লিকুইড ফ্লোটিং হোয়াইট পিল থাম্ব
        Box(
            modifier = Modifier
                .offset(x = currentOffsetDp)
                .align(Alignment.CenterStart)
                .width(thumbWidthDp)
                .height(34.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(17.dp))
                .clip(RoundedCornerShape(17.dp))
                .background(Color.White)
                .border(
                    width = 1.dp,
                    color = Color(0xFF00E5FF).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(17.dp)
                )
        )
    }
}
