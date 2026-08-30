package com.ourlauncher.app.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. Adaptive Luminance Glass Container
@Composable
fun AdaptiveLuminanceGlass(
    isLightBackground: Boolean,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val glassBaseColor = if (isLightBackground) {
        Color(0xFFE8F2FA).copy(alpha = 0.85f)
    } else {
        Color(0xFF10212E).copy(alpha = 0.72f)
    }

    val borderTopColor = if (isLightBackground) {
        Color.White.copy(alpha = 0.95f)
    } else {
        Color(0xFF00E5FF).copy(alpha = 0.65f)
    }

    val borderBottomColor = if (isLightBackground) {
        Color.White.copy(alpha = 0.35f)
    } else {
        Color.White.copy(alpha = 0.12f)
    }

    Box(
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(cornerRadius),
                ambientColor = Color.Black.copy(alpha = if (isLightBackground) 0.12f else 0.45f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    listOf(
                        glassBaseColor,
                        glassBaseColor.copy(alpha = if (isLightBackground) 0.92f else 0.85f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(borderTopColor, Color.White.copy(alpha = 0.2f), borderBottomColor)
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
    ) {
        // Specular top light edge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.5.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color.Transparent, borderTopColor, Color.Transparent)
                    )
                )
        )
        content()
    }
}

// 2. Liquid Mercury Floating Bottom Tabs
@Composable
fun LiquidGlassBottomTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabCount = tabs.size.coerceAtLeast(1)

    BoxWithConstraints(
        modifier = modifier
            .height(52.dp)
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF183042).copy(alpha = 0.78f),
                        Color(0xFF0C1924).copy(alpha = 0.88f)
                    )
                )
            )
            .border(
                1.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.55f), Color.White.copy(alpha = 0.15f))
                ),
                CircleShape
            )
            .padding(4.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val totalWidth = maxWidth
        val tabWidth = totalWidth / tabCount
        val indicatorOffset by animateDpAsState(
            targetValue = tabWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.75f, stiffness = 420f),
            label = "tabPill"
        )

        // Morphing Liquid Glass Active Pill
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF00A2FF), Color(0xFF0072FF))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
        )

        // Tab Labels
        Row(modifier = Modifier.fillMaxSize()) {
            tabs.forEachIndexed { index, title ->
                val isSelected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onTabSelected(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }
    }
}

// 3. Progressive Fading Glass Blur Overlay
@Composable
fun ProgressiveBlurBox(
    modifier: Modifier = Modifier,
    topAlpha: Float = 0.90f,
    bottomAlpha: Float = 0.05f,
    tintColor: Color = Color(0xFF0A1926)
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(
                        tintColor.copy(alpha = topAlpha),
                        tintColor.copy(alpha = (topAlpha + bottomAlpha) / 2f),
                        tintColor.copy(alpha = bottomAlpha)
                    )
                )
            )
    )
}

// 4. Liquid Glass Dynamic Scroll Container
@Composable
fun LiquidGlassScrollContainer(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 28.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val topRefractionAlpha = (scrollState.value / 250f).coerceIn(0f, 0.85f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF142938).copy(alpha = 0.75f),
                        Color(0xFF09141D).copy(alpha = 0.88f)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.verticalGradient(
                    listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.12f))
                ),
                RoundedCornerShape(cornerRadius)
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            content()
        }

        // Real-time top liquid edge highlight reacting to scroll position
        if (topRefractionAlpha > 0.01f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Color(0xFF00E5FF).copy(alpha = topRefractionAlpha),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
    }
}
