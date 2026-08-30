package com.ourlauncher.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
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
    height: Dp = 50.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    val stretchAnim = remember { Animatable(0f) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    val thumbWidthDp = 36.dp
    val thumbHeightDp = 14.dp
    val horizontalPaddingDp = 16.dp

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
                fun update(touchX: Float, dragDelta: Float = 0f) {
                    val padPx = horizontalPaddingDp.toPx()
                    val thumbWPx = thumbWidthDp.toPx()
                    val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                    val relX = (touchX - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                    val newFraction = relX / usableWidth
                    val rawVal = min + newFraction * (max - min)
                    val finalVal = if (steps > 0) {
                        val stepSize = (max - min) / (steps + 1)
                        ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                    } else rawVal
                    currentOnValueChange(finalVal.coerceIn(min, max))

                    if (abs(dragDelta) > 0.5f) {
                        coroutineScope.launch {
                            val target = (dragDelta * 0.22f).coerceIn(-12f, 12f)
                            stretchAnim.snapTo(target)
                            stretchAnim.animateTo(
                                0f,
                                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
                            )
                        }
                    }
                }

                detectTapGestures { offset -> update(offset.x) }
            }
            .pointerInput(valueRange, steps) {
                fun update(touchX: Float, dragDelta: Float) {
                    val padPx = horizontalPaddingDp.toPx()
                    val thumbWPx = thumbWidthDp.toPx()
                    val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                    val relX = (touchX - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                    val newFraction = relX / usableWidth
                    val rawVal = min + newFraction * (max - min)
                    val finalVal = if (steps > 0) {
                        val stepSize = (max - min) / (steps + 1)
                        ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                    } else rawVal
                    currentOnValueChange(finalVal.coerceIn(min, max))

                    coroutineScope.launch {
                        val target = (dragDelta * 0.35f).coerceIn(-14f, 14f)
                        stretchAnim.snapTo(target)
                        stretchAnim.animateTo(
                            0f,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
                        )
                    }
                }

                detectHorizontalDragGestures { change, dragAmount ->
                    change.consume()
                    update(change.position.x, dragAmount)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPaddingDp * 2) - thumbWidthDp).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPaddingDp + (usableWidthDp.value * fraction).dp + stretchAnim.value.dp
        val activeTrackWidthDp = (thumbOffsetDp + (thumbWidthDp / 2f) - horizontalPaddingDp).coerceAtLeast(0.dp)

        // ১. ইনঅ্যাক্টিভ ডার্ক ট্র্যাক
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPaddingDp)
                .height(3.dp)
                .clip(CircleShape)
                .background(Color(0xFF242426))
        )

        // ২. নিয়ন ব্লু অ্যাক্টিভ ট্র্যাক
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

        // ৩. ফ্লুইড ড্রপলেট স্ট্রেচ ক্যানভাস
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPaddingDp)
        ) {
            val centerY = size.height / 2f
            val thumbCenterXPx = (thumbOffsetDp - horizontalPaddingDp + (thumbWidthDp / 2f)).toPx()
            val stretch = stretchAnim.value * 2.5f

            if (abs(stretch) > 0.5f) {
                val blobPath = Path().apply {
                    val headX = thumbCenterXPx + stretch
                    val tailX = thumbCenterXPx - stretch * 0.6f
                    val radiusY = 7.dp.toPx()

                    moveTo(tailX, centerY - radiusY)
                    quadraticBezierTo(thumbCenterXPx, centerY - radiusY - (abs(stretch) * 0.25f), headX, centerY)
                    quadraticBezierTo(thumbCenterXPx, centerY + radiusY + (abs(stretch) * 0.25f), tailX, centerY + radiusY)
                    close()
                }
                drawPath(
                    path = blobPath,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF00E5FF).copy(alpha = 0.5f), Color(0xFF007AFF).copy(alpha = 0.65f))
                    )
                )
            }
        }

        // ৪. রিফ্লেকশন গ্লেয়ার
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .padding(horizontal = 20.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.35f), Color.Transparent)
                    )
                )
        )

        // ৫. ফ্লোটিং হোয়াইট পিল থাম্ব
        val dynamicWidth = (thumbWidthDp.value + abs(stretchAnim.value) * 0.4f).dp
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp - (abs(stretchAnim.value) * 0.2f).dp)
                .size(width = dynamicWidth, height = thumbHeightDp)
                .shadow(elevation = 6.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFF00E5FF).copy(alpha = 0.45f), CircleShape)
        )
    }
}
