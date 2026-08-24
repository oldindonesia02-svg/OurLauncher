package com.ourlauncher.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlin.math.cos
import kotlin.math.sin

private val bitmapCache = mutableMapOf<String, Bitmap>()

fun clearIconCache() {
    bitmapCache.clear()
}

fun getCachedBitmap(key: String, drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    bitmapCache[key]?.let { return it }
    return try {
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 144
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 144
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        bitmapCache[key] = bitmap
        bitmap
    } catch (e: Exception) {
        null
    }
}

@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    showLabel: Boolean = true,
    fontFamilyName: String = "sans-serif",
    iconSizeDp: Float = 54f,
    cornerRadiusPercent: Float = 25f,
    iconOpacity: Float = 1.0f,
    customDrawable: Drawable? = null,
    onClickWithBounds: ((Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settingsManager = remember { SettingsManager(context) }
    val targetDrawable = customDrawable ?: app.icon
    val cacheKey = "${app.packageName}_${targetDrawable?.hashCode() ?: 0}"
    val bitmap = remember(cacheKey) { getCachedBitmap(cacheKey, targetDrawable) }

    val shape = RoundedCornerShape(cornerRadiusPercent.toInt())
    var currentBounds by remember { mutableStateOf<Rect?>(null) }

    val colorFilter = remember(settingsManager.iconTheme, settingsManager.iconTintColor) {
        when (settingsManager.iconTheme) {
            "dark" -> {
                val matrix = ColorMatrix().apply { setToSaturation(0f) }
                ColorFilter.colorMatrix(matrix)
            }
            "tinted" -> ColorFilter.tint(Color(settingsManager.iconTintColor))
            else -> null
        }
    }

    val lensBrush = remember(settingsManager.lensLightEnabled, settingsManager.lensAngle, settingsManager.lensIntensity) {
        if (settingsManager.lensLightEnabled && settingsManager.graphicPreset != "low") {
            val rad = Math.toRadians(settingsManager.lensAngle.toDouble())
            val intensity = settingsManager.lensIntensity
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = (intensity * 0.85f).coerceIn(0f, 1f)),
                    Color.White.copy(alpha = (intensity * 0.15f).coerceIn(0f, 1f)),
                    Color.Transparent
                ),
                start = Offset.Zero,
                end = Offset(cos(rad).toFloat() * 200f, sin(rad).toFloat() * 200f)
            )
        } else null
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .onGloballyPositioned { coords ->
                if (onClickWithBounds != null) {
                    val b = coords.boundsInRoot()
                    currentBounds = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
                }
            }
            .clickable {
                if (onClickWithBounds != null && currentBounds != null) {
                    onClickWithBounds(currentBounds!!)
                } else {
                    onClick()
                }
            }
    ) {
        Box(
            modifier = Modifier
                .size(iconSizeDp.dp)
                .alpha(iconOpacity)
                .clip(shape)
                .then(
                    if (settingsManager.iconTheme == "transparent") {
                        Modifier.background(Color.White.copy(alpha = 0.12f))
                    } else Modifier
                )
                .then(
                    if (lensBrush != null) {
                        Modifier.border(settingsManager.lensStrokeWidth.dp, lensBrush, shape)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = app.label,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                fontSize = 11.5.sp,
                color = Color.White,
                fontFamily = when (fontFamilyName) {
                    "serif" -> FontFamily.Serif
                    "monospace" -> FontFamily.Monospace
                    else -> FontFamily.SansSerif
                },
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
