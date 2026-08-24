package com.ourlauncher.app.ui

import android.graphics.Rect
import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.AppInfo
import com.ourlauncher.app.SettingsManager
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun FolderIcon(
    folder: FolderInfo,
    onClick: () -> Unit,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable? = { null },
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(settingsManager.iconCornerRadius.toInt())

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
        modifier = modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(settingsManager.iconSize.dp)
                .alpha(settingsManager.iconOpacity)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.White.copy(alpha = 0.06f)
                        )
                    )
                )
                .then(
                    if (lensBrush != null) {
                        Modifier.border(settingsManager.lensStrokeWidth.dp, lensBrush, shape)
                    } else {
                        Modifier.border(1.dp, Color.White.copy(alpha = 0.15f), shape)
                    }
                )
                .padding(6.dp),
            contentAlignment = Alignment.Center
        ) {
            // 2x2 Mini Preview Icons
            val previewApps = folder.apps.take(4)
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    previewApps.getOrNull(0)?.let { MiniAppPreview(it, getCustomDrawable) }
                    previewApps.getOrNull(1)?.let { MiniAppPreview(it, getCustomDrawable) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    previewApps.getOrNull(2)?.let { MiniAppPreview(it, getCustomDrawable) }
                    previewApps.getOrNull(3)?.let { MiniAppPreview(it, getCustomDrawable) }
                }
            }
        }

        if (settingsManager.showLabels) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = folder.name,
                fontSize = 11.5.sp,
                color = Color.White,
                fontFamily = when (settingsManager.fontFamily) {
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

@Composable
fun MiniAppPreview(app: AppInfo, getCustomDrawable: (String) -> Drawable?) {
    val targetDrawable = getCustomDrawable(app.packageName) ?: app.icon
    val cacheKey = "${app.packageName}_${targetDrawable?.hashCode() ?: 0}"
    val bitmap = remember(cacheKey) { getCachedBitmap(cacheKey, targetDrawable) }

    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun FolderPopup(
    folder: FolderInfo,
    settingsManager: SettingsManager,
    getCustomDrawable: (String) -> Drawable? = { null },
    onAppClick: (AppInfo) -> Unit,
    onAppClickWithBounds: (AppInfo, Rect) -> Unit,
    onRenameFolder: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var folderTitle by remember { mutableStateOf(folder.name) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.65f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                focusManager.clearFocus()
                keyboardController?.hide()
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(320.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF2C2C2E).copy(alpha = 0.92f),
                            Color(0xFF161618).copy(alpha = 0.96f)
                        )
                    )
                )
                .border(
                    1.2.dp,
                    Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.35f),
                            Color.White.copy(alpha = 0.08f)
                        )
                    ),
                    RoundedCornerShape(32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { /* Consume taps on card */ }
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Editable Folder Title
                BasicTextField(
                    value = folderTitle,
                    onValueChange = {
                        folderTitle = it
                        onRenameFolder(it)
                    },
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    ),
                    cursorBrush = SolidColor(Color(0xFF0A84FF)),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                // Grid of Apps inside Folder
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth().heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(folder.apps, key = { it.packageName }) { app ->
                        AppIcon(
                            app = app,
                            onClick = {
                                onDismiss()
                                onAppClick(app)
                            },
                            showLabel = settingsManager.showLabels,
                            fontFamilyName = settingsManager.fontFamily,
                            iconSizeDp = settingsManager.iconSize,
                            cornerRadiusPercent = settingsManager.iconCornerRadius,
                            iconOpacity = settingsManager.iconOpacity,
                            customDrawable = getCustomDrawable(app.packageName),
                            onClickWithBounds = { bounds ->
                                onDismiss()
                                onAppClickWithBounds(app, bounds)
                            }
                        )
                    }
                }
            }
        }
    }
}
