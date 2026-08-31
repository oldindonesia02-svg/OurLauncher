package com.ourlauncher.app.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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

// Top-level Cache & Helper Functions
private val iconBitmapCache = mutableMapOf<String, Bitmap>()

fun clearIconCache() {
    iconBitmapCache.clear()
}

fun getCachedBitmap(context: Context, packageName: String): Bitmap? {
    return iconBitmapCache.getOrPut(packageName) {
        try {
            val drawable = context.packageManager.getApplicationIcon(packageName)
            drawableToBitmap(drawable)
        } catch (e: Exception) {
            return null
        }
    }
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable && drawable.bitmap != null) {
        return drawable.bitmap
    }
    val bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
        Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    showLabel: Boolean,
    fontFamilyName: String,
    iconSizeDp: Float,
    cornerRadiusPercent: Float,
    iconOpacity: Float,
    customDrawable: Drawable? = null,
    onClickWithBounds: ((Rect) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var itemBounds by remember { mutableStateOf<Rect?>(null) }

    val iconBitmap = remember(app.packageName, customDrawable) {
        if (customDrawable != null) {
            drawableToBitmap(customDrawable).asImageBitmap()
        } else {
            getCachedBitmap(context, app.packageName)?.asImageBitmap()
        }
    }

    val shape = RoundedCornerShape(percent = cornerRadiusPercent.toInt().coerceIn(0, 50))
    val isMonochromeTheme = fontFamilyName.equals("Monospace", ignoreCase = true)

    val customFontFamily = remember(fontFamilyName) {
        when (fontFamilyName.lowercase()) {
            "sf pro", "inter" -> FontFamily.SansSerif
            "monospace" -> FontFamily.Monospace
            "serif" -> FontFamily.Serif
            else -> FontFamily.Default
        }
    }

    Column(
        modifier = modifier
            .onGloballyPositioned { coords ->
                val b = coords.boundsInRoot()
                itemBounds = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (onClickWithBounds != null && itemBounds != null) {
                    onClickWithBounds(itemBounds!!)
                } else {
                    onClick()
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Liquid Glass Base Container
        Box(
            modifier = Modifier
                .size(iconSizeDp.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = shape,
                    spotColor = Color.Black.copy(alpha = 0.45f),
                    ambientColor = Color.Black.copy(alpha = 0.35f)
                )
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.15f * iconOpacity),
                            Color.Black.copy(alpha = 0.35f * iconOpacity)
                        )
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f * iconOpacity),
                            Color(0xFF00E5FF).copy(alpha = 0.30f * iconOpacity),
                            Color.White.copy(alpha = 0.10f * iconOpacity)
                        )
                    ),
                    shape = shape
                ),
            contentAlignment = Alignment.Center
        ) {
            // App Image Layer
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = app.label,
                    colorFilter = if (isMonochromeTheme) ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) }) else null,
                    modifier = Modifier
                        .fillMaxSize(0.88f)
                        .clip(shape)
                )
            }

            // Specular Liquid Glass Sheen Overlay (কাচের আলোর প্রতিফলন)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f * iconOpacity),
                                Color.Transparent
                            ),
                            radius = 90f
                        )
                    )
            )
        }

        // App Label
        if (showLabel) {
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = app.label,
                color = Color.White.copy(alpha = 0.95f),
                fontSize = 11.5.sp,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }
    }
}
