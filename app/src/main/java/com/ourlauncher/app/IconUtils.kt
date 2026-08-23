package com.ourlauncher.app

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * LauncherApps hands us android.graphics.drawable.Drawable icons (often AdaptiveIconDrawable).
 * Compose wants ImageBitmap/Painter. This rasterizes once; in a later phase we should
 * cache these (by packageName) instead of converting on every recomposition.
 */
fun Drawable.toImageBitmap(sizePx: Int = 128): ImageBitmap {
    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, sizePx, sizePx)
    draw(canvas)
    return bitmap.asImageBitmap()
}
