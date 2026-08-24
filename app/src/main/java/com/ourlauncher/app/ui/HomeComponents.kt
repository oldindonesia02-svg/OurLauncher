package com.ourlauncher.app.ui

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.AppRepository
import com.ourlauncher.app.SettingsManager
import kotlin.math.roundToInt

fun triggerPullDownAction(action: String, context: Context, onOpenSettings: () -> Unit) {
    when (action) {
        "notifications" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandNotificationsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "system_control_center" -> {
            try {
                val service = context.getSystemService("statusbar")
                val clz = Class.forName("android.app.StatusBarManager")
                clz.getMethod("expandSettingsPanel").invoke(service)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        "builtin_control_center" -> onOpenSettings()
    }
}

@Composable
fun HomeContextMenu(
    app: AppInfo,
    position: Offset,
    screenWidthPx: Float,
    repository: AppRepository,
    onDismiss: () -> Unit,
    onRemove: () -> Unit
) {
    val density = LocalDensity.current
    with(density) {
        val menuX = (position.x - 70.dp.toPx()).coerceIn(16.dp.toPx(), screenWidthPx - 180.dp.toPx())
        val menuY = (position.y - 140.dp.toPx()).coerceAtLeast(60.dp.toPx())

        Box(
            modifier = Modifier
                .offset { IntOffset(menuX.roundToInt(), menuY.roundToInt()) }
                .width(170.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF2C2C2E).copy(alpha = 0.92f), Color(0xFF1C1C1E).copy(alpha = 0.95f))
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(18.dp))
                .padding(6.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDismiss(); repository.openAppInfo(app.packageName) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("ⓘ", color = Color.White, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("App info", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onDismiss(); repository.uninstallApp(app.packageName) }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🗑", color = Color(0xFFFF453A), fontSize = 15.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Uninstall", color = Color(0xFFFF453A), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onRemove(); onDismiss() }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✕", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Remove", color = Color.White.copy(alpha = 0.85f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun AppLaunchOverlay(
    activeApp: AppInfo,
    activeBounds: Rect,
    progress: Float,
    screenWidthPx: Float,
    screenHeightPx: Float,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable?
) {
    val density = LocalDensity.current
    val currentX = activeBounds.left * (1f - progress)
    val currentY = activeBounds.top * (1f - progress)
    val currentW = activeBounds.width() + (screenWidthPx - activeBounds.width()) * progress
    val currentH = activeBounds.height() + (screenHeightPx - activeBounds.height()) * progress
    val initialCornerPx = (activeBounds.width() * (settingsManager.iconCornerRadius / 100f))
    val currentRadius = initialCornerPx * (1f - progress)

    with(density) {
        Box(
            modifier = Modifier
                .offset { IntOffset(currentX.roundToInt(), currentY.roundToInt()) }
                .size(currentW.toDp(), currentH.toDp())
                .clip(RoundedCornerShape(currentRadius.toDp()))
                .background(Color(0xFF141416))
                .graphicsLayer { alpha = progress.coerceIn(0.1f, 1f) },
            contentAlignment = Alignment.Center
        ) {
            val targetDrawable = getCustomDrawable(activeApp.packageName) ?: activeApp.icon
            val cacheKey = "${activeApp.packageName}_${targetDrawable.hashCode()}"
            val bitmap = getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()

            if (bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = null,
                    modifier = Modifier
                        .size((settingsManager.iconSize * 1.35f).dp)
                        .scale(1f + (0.35f * progress))
                )
            }
        }
    }
}

@Composable
fun PageIndicatorDots(totalPages: Int, currentPage: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalPages) { index ->
            val isSelected = currentPage == index
            val dotWidth by animateDpAsState(targetValue = if (isSelected) 16.dp else 6.dp, label = "dotWidth")

            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .height(6.dp)
                    .width(dotWidth)
                    .clip(CircleShape)
                    .background(if (isSelected) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.3f))
            )
        }
    }
}
