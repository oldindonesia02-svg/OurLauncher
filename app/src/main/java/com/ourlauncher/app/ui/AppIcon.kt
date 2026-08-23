package com.ourlauncher.app.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo

private val iconCache = HashMap<String, Bitmap>()

fun getCachedBitmap(packageName: String, drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    return iconCache.getOrPut(packageName) {
        if (drawable is BitmapDrawable && drawable.bitmap != null) {
            drawable.bitmap
        } else {
            val size = 108
            val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, size, size)
            drawable.draw(canvas)
            bitmap
        }
    }
}

@Composable
fun AppIcon(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    iconSizeDp: Int = 48,
    onClickWithBounds: ((Rect) -> Unit)? = null
) {
    var screenBounds by remember { mutableStateOf<Rect?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .onGloballyPositioned { coords ->
                val b = coords.boundsInWindow()
                screenBounds = Rect(b.left.toInt(), b.top.toInt(), b.right.toInt(), b.bottom.toInt())
            }
            .clickable {
                val bounds = screenBounds
                if (onClickWithBounds != null && bounds != null) {
                    onClickWithBounds(bounds)
                } else {
                    onClick()
                }
            }
            .padding(4.dp)
    ) {
        val imageBitmap = remember(app.packageName) {
            getCachedBitmap(app.packageName, app.icon)?.asImageBitmap()
        }

        if (imageBitmap != null) {
            Image(
                bitmap = imageBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(iconSizeDp.dp)
            )
        } else {
            Spacer(modifier = Modifier.size(iconSizeDp.dp))
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.label,
                color = Color.White,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
