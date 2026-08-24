package com.ourlauncher.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

private val iconCache = HashMap<String, Bitmap>()

fun clearIconCache() {
    iconCache.clear()
}

fun getCachedBitmap(cacheKey: String, drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    return iconCache.getOrPut(cacheKey) {
        val size = 192
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        bitmap
    }
}

@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    iconSizeDp: Float = 54f,
    cornerRadiusPercent: Float = 25f,
    iconOpacity: Float = 1.0f,
    customDrawable: Drawable? = null,
    onClickWithBounds: ((Rect) -> Unit)? = null
) {
    var screenBounds by remember { mutableStateOf<Rect?>(null) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .onGloballyPositioned { coords ->
                val b = coords.boundsInRoot()
                screenBounds = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                val bounds = screenBounds
                if (onClickWithBounds != null && bounds != null) {
                    onClickWithBounds(bounds)
                } else {
                    onClick()
                }
            }
            .padding(4.dp)
    ) {
        val targetDrawable = customDrawable ?: app.icon
        val cacheKey = "${app.packageName}_${targetDrawable.hashCode()}"
        val imageBitmap = remember(cacheKey) {
            getCachedBitmap(cacheKey, targetDrawable)?.asImageBitmap()
        }

        val shape = RoundedCornerShape(cornerRadiusPercent.toInt())

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(iconSizeDp.dp)
                    .clip(shape)
                    .alpha(iconOpacity)
            )
        } else {
            Spacer(modifier = Modifier.size(iconSizeDp.dp))
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = Color.White.copy(alpha = iconOpacity),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
