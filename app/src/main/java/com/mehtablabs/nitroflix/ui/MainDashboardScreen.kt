package com.mehtablabs.nitroflix.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.mehtablabs.nitroflix.network.StreamItem

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    streams: List<StreamItem>,
    navController: NavController
) {
    // 1. Extract unique categories from the JSON data
    val categories = remember(streams) {
        streams.map { it.category }.distinct().sorted()
    }
    
    var selectedCategory by remember { mutableStateOf("") }
    
    // Auto-select the first category when data loads
    LaunchedEffect(categories) {
        if (selectedCategory.isEmpty() && categories.isNotEmpty()) {
            selectedCategory = categories.first()
        }
    }

    // 2. Filter streams for the selected category
    val filteredStreams = remember(selectedCategory, streams) {
        streams.filter { it.category == selectedCategory }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        // LEFT SIDE: Professional Category Navigation Sidebar
        Column(
            modifier = Modifier
                .width(300.dp)
                .fillMaxHeight()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                    )
                )
                .padding(vertical = 32.dp)
        ) {
            // App Logo / Branding
            Row(
                modifier = Modifier.padding(horizontal = 32.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NITRO", color = Color(0xFFFF4500), fontSize = 28.sp, fontWeight = FontWeight.Black)
                Text("FLIX", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black)
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Vertical Category List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(categories) { category ->
                    CategoryNavItem(
                        title = category,
                        isSelected = selectedCategory == category,
                        onFocused = { selectedCategory = category }
                    )
                }
            }
        }

        // RIGHT SIDE: Dynamic Content Grid
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 48.dp, end = 24.dp)
        ) {
            // Category Title Header
            Text(
                text = selectedCategory.ifEmpty { "Loading..." },
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 32.dp, bottom = 16.dp)
            )

            // Responsive Channel Grid (4 columns)
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 48.dp, start = 32.dp, end = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(filteredStreams) { stream ->
                    ChannelGridCard(
                        stream = stream,
                        onClick = {
                            navController.navigate(Screen.Player.createRoute(stream.streamUrl))
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun CategoryNavItem(
    title: String,
    isSelected: Boolean,
    onFocused: () -> Unit
) {
    Surface(
        selected = isSelected,
        onClick = { onFocused() },
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .onFocusChanged { if (it.isFocused) onFocused() },
        scale = SelectableSurfaceDefaults.scale(focusedScale = 1.05f),
        colors = SelectableSurfaceDefaults.colors(
            containerColor = Color.Transparent,
            focusedContainerColor = Color.White.copy(alpha = 0.15f),
            selectedContainerColor = Color.White.copy(alpha = 0.1f),
            contentColor = if (isSelected) Color(0xFFFF4500) else Color.White.copy(alpha = 0.6f),
            focusedContentColor = Color.White
        ),
        shape = SelectableSurfaceDefaults.shape(shape = RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(3.dp, 18.dp)
                            .background(Color(0xFFFF4500), RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Text(
                    text = title,
                    fontSize = 17.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ChannelGridCard(
    stream: StreamItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .aspectRatio(16f / 9f)
            .fillMaxWidth(),
        shape = CardDefaults.shape(shape = RoundedCornerShape(12.dp)),
        scale = CardDefaults.scale(focusedScale = 1.12f),
        glow = CardDefaults.glow(focusedGlow = Glow(Color(0xFFFF4500).copy(alpha = 0.15f), 12.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = stream.posterUrl,
                contentDescription = stream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Subtle Bottom Shadow Gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                            startY = 120f
                        )
                    )
            )
            
            // Title Overlay
            Text(
                text = stream.title,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
