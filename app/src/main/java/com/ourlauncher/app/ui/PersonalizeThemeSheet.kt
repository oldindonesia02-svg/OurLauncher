package com.ourlauncher.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Chat
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ourlauncher.app.SettingsManager

data class ThemePreset(
    val id: String,
    val name: String,
    val bgGradients: List<Color>,
    val accentColor: Color,
    val iconShapeRadius: Float
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

    val themePresets = remember {
        listOf(
            ThemePreset(
                id = "default",
                name = "Default",
                bgGradients = listOf(Color(0xFFE2E6EC), Color(0xFF90A4AE), Color(0xFF37474F)),
                accentColor = Color(0xFF00A2FF),
                iconShapeRadius = 35f
            ),
            ThemePreset(
                id = "moto",
                name = "Moto",
                bgGradients = listOf(Color(0xFFFFB300), Color(0xFF0288D1), Color(0xFF1A237E)),
                accentColor = Color(0xFF00E5FF),
                iconShapeRadius = 45f
            ),
            ThemePreset(
                id = "seaway",
                name = "Seaway",
                bgGradients = listOf(Color(0xFF00897B), Color(0xFF26A69A), Color(0xFFE65100)),
                accentColor = Color(0xFF00E676),
                iconShapeRadius = 25f
            ),
            ThemePreset(
                id = "liquid_dark",
                name = "Liquid Neon",
                bgGradients = listOf(Color(0xFF142634), Color(0xFF09141D), Color(0xFF000508)),
                accentColor = Color(0xFF00E5FF),
                iconShapeRadius = 30f
            ),
            ThemePreset(
                id = "sunset_glow",
                name = "Cyberpunk",
                bgGradients = listOf(Color(0xFFFF007F), Color(0xFF7B1FA2), Color(0xFF12002B)),
                accentColor = Color(0xFFFF007F),
                iconShapeRadius = 50f
            ),
            ThemePreset(
                id = "minimal_pure",
                name = "Minimalist",
                bgGradients = listOf(Color(0xFF2C3440), Color(0xFF1E242B), Color(0xFF111417)),
                accentColor = Color.White,
                iconShapeRadius = 20f
            )
        )
    }

    val fontList = remember {
        listOf("Default", "Inter", "Roboto", "Poppins", "Outfit", "SF Pro", "Monospace")
    }

    var currentThemeId by remember { mutableStateOf("moto") }
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
        // Floating Central Glass Modal
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 40.dp)
                .fillMaxWidth()
                .heightIn(max = 580.dp)
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
                            Color(0xFF182A3A).copy(alpha = 0.94f),
                            Color(0xFF0E1A24).copy(alpha = 0.97f)
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
                        .padding(horizontal = 30.dp, vertical = 6.dp),
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

                // Content View based on Tab
                if (selectedTab == 0) {
                    // Themes Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(themePresets, key = { it.id }) { preset ->
                            val isSelected = preset.id == currentThemeId
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable {
                                        currentThemeId = preset.id
                                        settingsManager.iconCornerRadius = preset.iconShapeRadius
                                        onApplyTheme(preset)
                                    }
                            ) {
                                // Miniature Phone Mockup Card
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
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
                                    // 2x2 Mini Icons Grid Preview
                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            MiniGlassIcon(Icons.Rounded.Schedule, Color(0xFF0072FF))
                                            MiniGlassIcon(Icons.Rounded.Image, Color(0xFF00E676))
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            MiniGlassIcon(Icons.Rounded.Person, Color(0xFF00B0FF))
                                            MiniGlassIcon(Icons.Rounded.Chat, Color(0xFF2979FF))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = preset.name,
                                    color = if (isSelected) Color(0xFF00E5FF) else Color.White.copy(alpha = 0.85f),
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                } else {
                    // Fonts List
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
                                        settingsManager.fontFamily = fontName
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

                // Apply / Done Button
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
fun MiniGlassIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, bgColor: Color) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(bgColor.copy(alpha = 0.85f))
            .border(0.8.dp, Color.White.copy(alpha = 0.6f), CircleShape),
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
