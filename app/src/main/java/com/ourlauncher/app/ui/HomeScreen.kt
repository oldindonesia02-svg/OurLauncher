@Composable
fun VoidBottomBar(
    dockApps: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onSettingsClick: () -> Unit,
    dockRadius: Float,
    showDockBg: Boolean,
    searchOffset: Float,
    showAssistant: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.offset(y = searchOffset.dp) // Live Move Search Bar!
        ) {
            // Search Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                    .clickable { onOpenDrawer() }
                    .padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Text("search", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
            }

            // Small Assistant/Hex Button
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape)
                    .clickable { onSettingsClick() },
                contentAlignment = Alignment.Center
            ) {
                Text("⬡", color = Color.White, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Liquid Glass Dock
        val dockShape = RoundedCornerShape(dockRadius.dp)
        Box(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(dockShape)
                .then(
                    if (showDockBg) {
                        Modifier
                            .background(Color.White.copy(alpha = 0.2f))
                            .border(1.dp, Color.White.copy(alpha = 0.4f), dockShape)
                    } else Modifier
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                dockApps.forEach { app ->
                    AppIcon(app = app, onClick = { onAppClick(app) }, showLabel = false, iconSizeDp = 48)
                }
            }
        }
    }
}
