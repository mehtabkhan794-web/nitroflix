package com.mehtablabs.nitroflix.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import coil.compose.AsyncImage

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var selectedTab by remember { mutableStateOf("Clear Cache") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0E0E0E))
    ) {
        // Vignette effect
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.8f)
                    )
                )
        )

        Column(modifier = Modifier.fillMaxSize()) {
            SettingsHeader()

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp)
            ) {
                // Sidebar (30%)
                SettingsSidebar(
                    modifier = Modifier.weight(0.3f),
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )

                // Main Panel (70%)
                SettingsMainPanel(
                    modifier = Modifier.weight(0.7f),
                    selectedTab = selectedTab
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 48.dp, bottom = 40.dp)
        ) {
            NavigationControllerHints()
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 48.dp, vertical = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "NitroFlix",
                style = MaterialTheme.typography.displayMedium,
                color = Color(0xFFFFB4A2),
                fontWeight = FontWeight.Black,
                letterSpacing = (-2).sp
            )
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(32.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Medium
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Text(
                text = "10:45 PM",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold
            )
            AsyncImage(
                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuDs3vUU_84GUimzAOPDUJPeJkf2tpWp_B0h15nh1bVlNJEnGS3j5vDRWk7gp378ArkPK4cfQTm3murAYhzUvtII1EsK0J3CX1dcbeChSHWvIUbJ3ROtVb0M-7fblApn0shRQjjvFscYBBqmAIKA748J-q65kWO02A8liQ55VjVu3sRoxI2djbhj5GoKE0_vGGELkZ8_gBmk2q5lXSwXkOkf41AhO4KTVltv1cqZeOYnsEKKXz5v2A0rVaDzAZ_kwrb0UDrIQ5j9ong",
                contentDescription = "Profile",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFFFFB4A2).copy(alpha = 0.4f), CircleShape),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun SettingsSidebar(
    modifier: Modifier = Modifier,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Column(
        modifier = modifier.padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "ACCOUNT",
            style = MaterialTheme.typography.labelLarge,
            color = Color.White.copy(alpha = 0.4f),
            fontWeight = FontWeight.Bold,
            letterSpacing = 4.sp,
            modifier = Modifier.padding(bottom = 16.dp, start = 24.dp)
        )

        SidebarItem("Account Profile", selectedTab, onTabSelected)
        SidebarItem("App Updates", selectedTab, onTabSelected, badge = "1")
        SidebarItem("Subscription Plan", selectedTab, onTabSelected)
        SidebarItem("Clear Cache", selectedTab, onTabSelected, isDelete = true)
        SidebarItem("About NitroFlix", selectedTab, onTabSelected)
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SidebarItem(
    text: String,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    badge: String? = null,
    isDelete: Boolean = false
) {
    val isSelected = selectedTab == text
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val shape = RoundedCornerShape(16.dp)

    Surface(
        onClick = { onTabSelected(text) },
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth(),
        shape = ClickableSurfaceDefaults.shape(shape),
        scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = if (isSelected && !isDelete) Color(0xFF2A2A2A) else Color(0xFF1C1B1B),
            focusedContainerColor = if (isDelete) Color.Transparent else Color(0xFF353535)
        ),
        border = ClickableSurfaceDefaults.border(
            focusedBorder = if (isDelete) Border(BorderStroke(2.dp, Color(0xFFFD6C00)), shape = shape) 
                            else Border(BorderStroke(2.dp, Color.White.copy(alpha = 0.5f)), shape = shape)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if ((isSelected || isFocused) && isDelete) {
                        Modifier.background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFFD6C00).copy(alpha = 0.4f), Color(0xFFFD6C00).copy(alpha = 0.1f))
                            )
                        )
                    } else Modifier
                )
                .padding(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(24.dp).background(if(isDelete) Color(0xFFFD6C00) else if (isFocused) Color.White else Color.Gray, CircleShape))
                    Spacer(modifier = Modifier.width(24.dp))
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = if (isFocused || isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isFocused || isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                    )
                }
                if (badge != null) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFB4AB), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(badge, color = Color(0xFF690005), fontSize = 12.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsMainPanel(
    modifier: Modifier = Modifier,
    selectedTab: String
) {
    Box(
        modifier = modifier
            .padding(start = 48.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(40.dp))
            .background(Color(0xFF131313).copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(40.dp))
            .padding(48.dp)
    ) {
        if (selectedTab == "Clear Cache") {
            ClearCacheContent()
        } else {
            Text("Content for $selectedTab", color = Color.White)
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ClearCacheContent() {
    Column(verticalArrangement = Arrangement.spacedBy(40.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Clear Cache",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Optimize your viewing experience by clearing temporary data. This process is safe and will not delete your saved movies or profile settings.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "45.35",
                    fontSize = 100.sp,
                    color = Color(0xFFFD6C00),
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-4).sp
                )
                Text(
                    text = "MEGABYTES USED",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White.copy(alpha = 0.4f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .border(1.dp, Color.White.copy(alpha = 0.05f), RoundedCornerShape(24.dp))
                .padding(32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("OPTIMIZATION STATUS", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.4f), letterSpacing = 2.sp)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.size(12.dp).background(Color(0xFFFD6C00), CircleShape))
                            Text("82% Peak Efficiency", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Text("Cache accumulation trend (Last 7 days)", style = MaterialTheme.typography.labelLarge, color = Color.White.copy(alpha = 0.4f))
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = listOf(0.4f, 0.55f, 0.7f, 0.9f, 0.65f, 0.5f, 0.3f)
                    heights.forEachIndexed { index, h ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(h)
                                .background(
                                    if (index == 3) Color(0xFFFD6C00).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f),
                                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                                .then(
                                    if (index == 3) Modifier.border(1.dp, Color(0xFFFD6C00), RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)) else Modifier
                                )
                        ) {
                            if (index == 3) {
                                Text(
                                    "PEAK",
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(top = 4.dp)
                                        .background(Color(0xFFFD6C00), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            Surface(
                onClick = {},
                modifier = Modifier.padding(end = 24.dp),
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(28.dp)),
                colors = ClickableSurfaceDefaults.colors(containerColor = Color.White.copy(alpha = 0.1f))
            ) {
                Text("View Details", modifier = Modifier.padding(horizontal = 48.dp, vertical = 24.dp), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Surface(
                onClick = {},
                shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(28.dp)),
                colors = ClickableSurfaceDefaults.colors(containerColor = Color(0xFFFD6C00)),
                scale = ClickableSurfaceDefaults.scale(focusedScale = 1.05f)
            ) {
                Text("DEEP CLEAN STORAGE", modifier = Modifier.padding(horizontal = 64.dp, vertical = 24.dp), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color.Black)
            }
        }
    }
}

@Composable
fun NavigationControllerHints() {
    Row(
        modifier = Modifier.alpha(0.6f),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HintButtonItem("A", "Select")
        HintButtonItem("B", "Back")
    }
}

@Composable
fun HintButtonItem(key: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(key, color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(label.uppercase(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}
