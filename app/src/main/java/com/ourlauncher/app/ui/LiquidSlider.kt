package com.ourlauncher.app.ui.liquidglass

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

private var cachedWallpaper: Bitmap? = null

private fun getWallpaperBitmapCached(context: android.content.Context): Bitmap? {
    cachedWallpaper?.let { return it }
    return try {
        val drawable = android.app.WallpaperManager.getInstance(context).drawable ?: return null
        val bmp = (drawable as? BitmapDrawable)?.bitmap ?: run {
            val w = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1080
            val h = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1920
            val b = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(b)
            drawable.setBounds(0, 0, w, h)
            drawable.draw(canvas)
            b
        }
        cachedWallpaper = bmp
        bmp
    } catch (e: Exception) {
        null
    }
}

/**
 * Liquid-glass styled slider. The thumb shows a real GPU-blurred (RenderEffect,
 * Android 12+) slice of the wallpaper, giving a genuine frosted-glass look.
 * On older Android versions it falls back to a plain translucent white thumb.
 * No extra library or Kotlin-version bump required.
 */
@Composable
fun LiquidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val wallpaper = remember { getWallpaperBitmapCached(context) }

    var trackWidthPx by remember { mutableIntStateOf(0) }
    val thumbSizeDp = 32.dp
    val thumbSizePx = with(density) { thumbSizeDp.toPx() }

    val fraction = ((value - valueRange.start) / (valueRange.endInclusive - valueRange.start)).coerceIn(0f, 1f)
    val maxTravel = (trackWidthPx - thumbSizePx).coerceAtLeast(0f)
    val thumbOffsetPx = fraction * maxTravel

    fun updateFromOffset(offsetPx: Float) {
        val clamped = offsetPx.coerceIn(0f, maxTravel)
        val newFraction = if (maxTravel > 0f) clamped / maxTravel else 0f
        onValueChange(valueRange.start + newFraction * (valueRange.endInclusive - valueRange.start))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .onGloballyPositioned { trackWidthPx = it.size.width }
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .pointerInput(trackWidthPx) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    updateFromOffset(thumbOffsetPx + dragAmount.x)
                }
            },
        contentAlignment = Alignment.CenterStart
    ) {
        val fillWidthPx = (thumbOffsetPx + thumbSizePx / 2).coerceIn(0f, trackWidthPx.toFloat())
        Box(
            modifier = Modifier
                .size(width = with(density) { fillWidthPx.toDp() }, height = 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF0A84FF).copy(alpha = 0.35f))
        )

        Box(
            modifier = Modifier
                .offset { IntOffset(thumbOffsetPx.roundToInt(), 0) }
                .size(thumbSizeDp)
        ) {
            if (wallpaper != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Image(
                    bitmap = wallpaper.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(thumbSizeDp)
                        .clip(CircleShape)
                        .blur(10.dp)
                        .background(Color.White.copy(alpha = 0.25f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(thumbSizeDp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.85f))
                )
            }
        }
    }
}
