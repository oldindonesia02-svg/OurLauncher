package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

data class ThemePreset(
    val id: String,
    val name: String,
    val bgGradients: List<Color>,
    val accentColor: Color,
    val iconShapeRadius: Float,
    val fontFamily: String,
    val iconSizeDp: Float = 60f
)

@Composable
fun PersonalizeThemeSheet(
    settingsManager: SettingsManager,
    onApplyTheme: (ThemePreset) -> Unit,
    onApplyFont: (String) -> Unit,
    onDismiss: () -> Unit
) {
    BackHandler { onDismiss() }

    var selectedTab by remember { mutableIntStateOf(0) } // 0 -> Themes, 1 -> Font

    // Real OS Themes requested
    val themePresets = remember {
        listOf(
            ThemePreset(
                id = "ios_dark_liquid",
                name = "iOS Dark Liquid",
                bgGradients = listOf(Color(0xFF1C1C1E), Color(0xFF000000), Color(0xFF2C2C2E)),
                accentColor = Color(0xFF0A84FF),
                iconShapeRadius = 42f, // iOS Squircle Curve
                fontFamily = "SF Pro",
                iconSizeDp = 60f
            ),
            ThemePreset(
                id = "ios_original_liquid",
                name = "iOS Original Glass",
                bgGradients = listOf(Color(0xFF64B5F6), Color(0xFF1976D2), Color(0xFF0D47A1)),
                accentColor = Color(0xFF007AFF),
                iconShapeRadius = 42f,
                fontFamily = "SF Pro",
                iconSizeDp = 60f
            ),
            ThemePreset(
                id = "hyper_os",
                name = "HyperOS Theme",
                bgGradients = listOf(Color(0xFFFF7043), Color(0xFFF4511E), Color(0xFF212121)),
                accentColor = Color(0xFFFF6D00),
                iconShapeRadius = 32f, // Xiaomi HyperOS Smooth Rounded
                fontFamily = "Inter",
                iconSizeDp = 58f
            ),
            ThemePreset(
                id = "iqoo_monster",
                name = "iQOO Monster",
                bgGradients = listOf(Color(0xFFFFD600), Color(0xFF212121), Color(0xFF000000)),
                accentColor = Color(0xFFFFD600),
                iconShapeRadius = 26f, // iQOO Sporty Radius
                fontFamily = "Roboto",
                iconSizeDp = 62f
            ),
            ThemePreset(
                id = "moto_liquid",
                name = "Moto Liquid Glass",
                bgGradients = listOf(Color(0xFF00E5FF), Color(0xFF0072FF), Color(0xFF09141D)),
                accentColor = Color(0xFF00E5FF),
                iconShapeRadius = 50f, // Moto Circle
                fontFamily = "Outfit",
                iconSizeDp = 60f
            ),
            ThemePreset(
                id = "nothing_os",
                name = "Nothing OS Glass",
                bgGradients = listOf(Color(0xFF2B2B2B), Color(0xFF121212), Color(0xFF000000)),
                accentColor = Color(0xFFFF3B30),
                iconShapeRadius = 50f, // Nothing OS Dot/Circle
                fontFamily = "Monospace",
                iconSizeDp = 56f
            )
        )
    }

    val fontList = remember {
        listOf("Default", "SF Pro", "Inter", "Roboto", "Poppins", "Outfit", "Monospace")
    }

    var selectedThemeId by remember { mutableStateOf("moto_liquid") }
    var currentFont by remember { mutableStateOf(settingsManager.fontFamily) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.52f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        // Center Glass Modal Card
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 36.dp)
                .fillMaxWidth()
                .heightIn(max = 600.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(32.dp),
                    spotColor = Color(0xFF00E5FF).copy(alpha = 0.35f),
                    ambientColor = Color.Black.copy(alpha = 0.6f)
                )
                .clip(RoundedCornerShape(32.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF182A3A).copy(alpha = 0.95f),
                            Color(0xFF0E1A24).copy(alpha = 0.98f)
                        )
                    )
                )
                .border(
                    width = 1.3.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color(0xFF00E5FF).copy(alpha = 0.40f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(32.dp)
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Tab Switcher (Themes | Font)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Themes Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { selectedTab = 0 }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Themes",
                            color = if (selectedTab == 0) Color.White else Color.White.copy(alpha = 0.55f),
                            fontSize = 17.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedTab == 0) {
                            Box(
                                modifier = Modifier
                                    .width(36.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF))
                            )
                        }
                    }

                    // Font Tab
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { selectedTab = 1 }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Font",
                            color = if (selectedTab == 1) Color.White else Color.White.copy(alpha = 0.55f),
                            fontSize = 17.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        if (selectedTab == 1) {
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(3.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E5FF))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Content
                if (selectedTab == 0) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(themePresets, key = { it.id }) { preset ->
                            val isSelected = preset.id == selectedThemeId
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedThemeId = preset.id
                                        onApplyTheme(preset)
                                    }
                            ) {
                                // Miniature Card with Live Theme Shape Preview
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Brush.verticalGradient(preset.bgGradients))
                                        .border(
                                            width = if (isSelected) 2.5.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(20.dp)
                                        )
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            MiniThemeIcon(Icons.Rounded.Call, preset.accentColor, preset.iconShapeRadius)
                                            MiniThemeIcon(Icons.Rounded.Image, Color(0xFF34C759), preset.iconShapeRadius)
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            MiniThemeIcon(Icons.Rounded.CameraAlt, Color(0xFFFF9500), preset.iconShapeRadius)
                                            MiniThemeIcon(Icons.Rounded.Chat, Color(0xFF5856D6), preset.iconShapeRadius)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.name,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(fontList) { fontName ->
                            val isSelected = fontName == currentFont
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSelected) Color(0xFF007BFF).copy(alpha = 0.22f) else Color.White.copy(alpha = 0.06f))
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.12f),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        currentFont = fontName
                                        onApplyFont(fontName)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = fontName,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                if (isSelected) {
                                    Icon(Icons.Rounded.Check, contentDescription = null, tint = Color(0xFF00E5FF))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Done Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF00A2FF), Color(0xFF0072FF))
                            )
                        )
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MiniThemeIcon(icon: ImageVector, bgColor: Color, radiusPercent: Float) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(RoundedCornerShape(percent = radiusPercent.toInt()))
            .background(bgColor.copy(alpha = 0.85f))
            .border(0.8.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(percent = radiusPercent.toInt())),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(13.dp)
        )
    }
}
