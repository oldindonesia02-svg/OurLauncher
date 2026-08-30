package com.ourlauncher.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
    height: Dp = 48.dp
) {
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    var widthPx by remember { mutableFloatStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    val touchGlow = remember { Animatable(0f) }
    val stretchAnim = remember { Animatable(0f) }

    val min = valueRange.start
    val max = valueRange.endInclusive
    val fraction = if (max > min) ((value - min) / (max - min)).coerceIn(0f, 1f) else 0f

    val thumbWidthDp = 34.dp
    val thumbHeightDp = 14.dp
    val horizontalPaddingDp = 14.dp

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .onSizeChanged { widthPx = it.width.toFloat() }
            .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = Color.Black.copy(alpha = 0.25f))
            .clip(CircleShape)
            // Liquid Frosted Glass Capsule (Translucent)
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
                        coroutineScope.launch {
                            touchGlow.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
                        }
                        val padPx = horizontalPaddingDp.toPx()
                        val thumbWPx = thumbWidthDp.toPx()
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
                        coroutineScope.launch {
                            touchGlow.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow))
                        }
                    }
                )
            }
            .pointerInput(valueRange, steps) {
                detectDragGestures(
                    onDragStart = {
                        coroutineScope.launch {
                            touchGlow.animateTo(1f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
                        }
                    },
                    onDragEnd = {
                        coroutineScope.launch {
                            touchGlow.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow))
                            stretchAnim.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            touchGlow.animateTo(0f, spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow))
                            stretchAnim.animateTo(0f, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMedium))
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val padPx = horizontalPaddingDp.toPx()
                        val thumbWPx = thumbWidthDp.toPx()
                        val usableWidth = (widthPx - (padPx * 2) - thumbWPx).coerceAtLeast(1f)
                        val relX = (change.position.x - padPx - (thumbWPx / 2f)).coerceIn(0f, usableWidth)
                        val newFraction = relX / usableWidth
                        val rawVal = min + newFraction * (max - min)
                        val finalVal = if (steps > 0) {
                            val stepSize = (max - min) / (steps + 1)
                            ((rawVal - min) / stepSize).roundToInt() * stepSize + min
                        } else rawVal
                        currentOnValueChange(finalVal.coerceIn(min, max))

                        coroutineScope.launch {
                            val target = (dragAmount.x * 0.5f).coerceIn(-20f, 20f)
                            stretchAnim.snapTo(target)
                            stretchAnim.animateTo(0f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMedium))
                        }
                    }
                )
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val usableWidthDp = (maxWidth - (horizontalPaddingDp * 2) - thumbWidthDp).coerceAtLeast(0.dp)
        val thumbOffsetDp = horizontalPaddingDp + (usableWidthDp.value * fraction).dp + stretchAnim.value.dp
        val activeTrackWidthDp = (thumbOffsetDp + (thumbWidthDp / 2f) - horizontalPaddingDp).coerceAtLeast(0.dp)

        // 1. Inactive Frosted Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPaddingDp)
                .height(3.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
        )

        // 2. Active Neon Cyan Glow Track
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

        // 3. Fluid Droplet Stretch & Touch Refraction Canvas
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = horizontalPaddingDp)
        ) {
            val centerY = size.height / 2f
            val thumbCenterXPx = (thumbOffsetDp - horizontalPaddingDp + (thumbWidthDp / 2f)).toPx()
            val stretch = stretchAnim.value * 2.6f

            // Liquid Droplet Teardrop Tail on drag
            if (abs(stretch) > 0.3f) {
                val blobPath = Path().apply {
                    val headX = thumbCenterXPx + stretch
                    val tailX = thumbCenterXPx - stretch * 0.7f
                    val radiusY = 8.dp.toPx()

                    moveTo(tailX, centerY - radiusY)
                    quadraticBezierTo(thumbCenterXPx, centerY - radiusY - (abs(stretch) * 0.35f), headX, centerY)
                    quadraticBezierTo(thumbCenterXPx, centerY + radiusY + (abs(stretch) * 0.35f), tailX, centerY + radiusY)
                    close()
                }
                drawPath(
                    path = blobPath,
                    brush = Brush.horizontalGradient(
                        listOf(Color(0xFF00E5FF).copy(alpha = 0.7f), Color(0xFF007AFF).copy(alpha = 0.85f))
                    )
                )
            }

            // Glass Lens Halo & Reflection when Touched
            if (touchGlow.value > 0.01f) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF00E5FF).copy(alpha = 0.55f * touchGlow.value),
                            Color.White.copy(alpha = 0.28f * touchGlow.value),
                            Color.Transparent
                        ),
                        center = Offset(thumbCenterXPx, centerY),
                        radius = 32.dp.toPx()
                    ),
                    radius = 32.dp.toPx(),
                    center = Offset(thumbCenterXPx, centerY)
                )
            }
        }

        // 4. Specular Reflection
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

        // 5. Floating White Pill Thumb
        val dynamicWidth = (thumbWidthDp.value + abs(stretchAnim.value) * 0.4f).dp
        Box(
            modifier = Modifier
                .offset(x = thumbOffsetDp - (abs(stretchAnim.value) * 0.2f).dp)
                .size(width = dynamicWidth, height = thumbHeightDp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.2.dp, Color(0xFF00E5FF).copy(alpha = 0.8f), CircleShape)
        )
    }
}
