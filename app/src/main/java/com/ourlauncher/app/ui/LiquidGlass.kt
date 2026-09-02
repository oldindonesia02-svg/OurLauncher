package com.ourlauncher.app.ui.components

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ourlauncher.app.SettingsManager
import kotlinx.coroutines.launch

fun Modifier.liquidGlassEffect(
    settings: SettingsManager,
    cornerRadius: Dp = 28.dp,
    isDarkTheme: Boolean = false
): Modifier = this.then(
    Modifier
        .clip(RoundedCornerShape(cornerRadius))
        .graphicsLayer {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && settings.glassBlurRadius > 0f) {
                renderEffect = RenderEffect.createBlurEffect(
                    settings.glassBlurRadius,
                    settings.glassBlurRadius,
                    Shader.TileMode.CLAMP
                ).asComposeRenderEffect()
            }
            alpha = settings.glassTransparency.coerceIn(0.1f, 1f)
        }
        .background(
            color = if (isDarkTheme) {
                Color(0xFF151518).copy(alpha = settings.glassTintAlpha)
            } else {
                Color(0xFFFFFFFF).copy(alpha = settings.glassTintAlpha)
            },
            shape = RoundedCornerShape(cornerRadius)
        )
        .border(
            width = 1.2.dp,
            brush = Brush.linearGradient(
                colors = if (settings.enableRainbowSheen) {
                    listOf(
                        Color.White.copy(alpha = settings.specularHighlight),
                        Color(0xFFE0E7FF).copy(alpha = 0.35f),
                        Color(0xFFFCE7F3).copy(alpha = 0.20f),
                        Color.White.copy(alpha = settings.specularHighlight * 0.4f)
                    )
                } else {
                    listOf(
                        Color.White.copy(alpha = settings.specularHighlight),
                        Color.White.copy(alpha = 0.15f),
                        Color.Transparent,
                        Color.White.copy(alpha = settings.specularHighlight * 0.3f)
                    )
                },
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
        .drawWithContent {
            drawContent()
            if (settings.glassDepthEnabled) {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.05f)
                        )
                    )
                )
            }
        }
)

@Composable
fun LiquidGlassSurface(
    settings: SettingsManager,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    isDarkTheme: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val scale = remember { Animatable(1f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale.value
                scaleY = scale.value
            }
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitFirstDown(requireUnconsumed = false)
                        scope.launch {
                            scale.animateTo(
                                targetValue = 0.96f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            )
                        }
                        waitForUpOrCancellation()
                        scope.launch {
                            scale.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        onClick?.invoke()
                    }
                }
            }
            .liquidGlassEffect(
                settings = settings,
                cornerRadius = cornerRadius,
                isDarkTheme = isDarkTheme
            ),
        content = content
    )
}
