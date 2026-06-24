package com.mehtablabs.nitroflix.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.tv.foundation.lazy.list.TvLazyColumn
import androidx.tv.foundation.lazy.list.TvLazyRow
import androidx.tv.foundation.lazy.list.items
import androidx.tv.foundation.lazy.list.itemsIndexed
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.mehtablabs.nitroflix.network.StreamItem

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainDashboardScreen(
    streams: List<StreamItem>,
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isTabRowFocused by remember { mutableStateOf(false) }
    
    val tabs = listOf(
        "Home" to Screen.Dashboard.route,
        "Movies" to Screen.Movies.route,
        "Web Series" to Screen.WebSeries.route,
        "Sports" to Screen.Sports.route,
        "Live TV" to Screen.LiveTV.route
    )

    val categorizedStreams = remember(streams) {
        streams.groupBy { it.category }
    }

    val featuredItem = remember(streams) {
        streams.firstOrNull() ?: StreamItem(
            title = "Tears of Steel",
            category = "Sci-Fi Action",
            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1925&auto=format&fit=crop",
            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505))
    ) {
        TvLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 56.dp)
        ) {
            // 1. Top Navigation / Logo Header
            // Integrated as an item in the TvLazyColumn for smooth vertical focus flow
            item {
                TopGradientHeader(onSettingsSelected = {
                    navController.navigate(Screen.Settings.route) {
                        launchSingleTop = true
                    }
                })
            }

            // 2. Featured Hero Banner
            item {
                HeroBanner(
                    item = featuredItem,
                    onPlayClicked = { stream ->
                        navController.navigate(Screen.Player.createRoute(stream.streamUrl))
                    },
                    onMoreInfoClicked = { stream ->
                        navController.navigate(Screen.Details.createRoute(stream.title))
                    }
                )
            }

            // 3. Horizontal Navigation Tabs
            item {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = Color.Transparent,
                    indicator = { tabPositions, _ ->
                        TabRowDefaults.PillIndicator(
                            currentTabPosition = tabPositions[selectedTabIndex],
                            doesTabRowHaveFocus = isTabRowFocused,
                            activeColor = Color(0xFFFF4500),
                            inactiveColor = Color.Transparent
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 56.dp, vertical = 16.dp)
                        .onFocusChanged { isTabRowFocused = it.hasFocus }
                ) {
                    tabs.forEachIndexed { index, (title, route) ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onFocus = { selectedTabIndex = index },
                            onClick = {
                                selectedTabIndex = index
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            colors = TabDefaults.pillIndicatorTabColors(
                                contentColor = Color.White.copy(alpha = 0.5f),
                                selectedContentColor = Color.White,
                                focusedContentColor = Color.White
                            )
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // 4. Dynamic Categories from API/Test Streams
            items(categorizedStreams.keys.toList()) { category ->
                StreamRow(
                    title = category,
                    streams = categorizedStreams[category] ?: emptyList(),
                    onStreamSelected = { stream ->
                        navController.navigate(Screen.Player.createRoute(stream.streamUrl))
                    }
                )
            }

            // 5. Premium Placeholder Rows for Movies & Web Series
            item {
                StreamRow(
                    title = "Trending Now",
                    streams = emptyList(),
                    onStreamSelected = { stream ->
                        navController.navigate(Screen.Player.createRoute(stream.streamUrl))
                    }
                )
            }

            item {
                StreamRow(
                    title = "New Releases",
                    streams = emptyList(),
                    onStreamSelected = { stream ->
                        navController.navigate(Screen.Player.createRoute(stream.streamUrl))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HeroBanner(
    item: StreamItem,
    onPlayClicked: (StreamItem) -> Unit,
    onMoreInfoClicked: (StreamItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(540.dp)
    ) {
        AsyncImage(
            model = item.posterUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Multi-layered Cinematic Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Black.copy(alpha = 0.6f),
                        0.3f to Color.Transparent,
                        0.7f to Color(0xFF050505).copy(alpha = 0.5f),
                        1.0f to Color(0xFF050505)
                    )
                )
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        0.0f to Color.Black.copy(alpha = 0.8f),
                        0.5f to Color.Transparent
                    )
                )
        )

        // Banner Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 56.dp, bottom = 60.dp)
                .widthIn(max = 600.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF4500), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text("TOP 10", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.category.uppercase(),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.title,
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 62.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Text(
                text = "Experience the next generation of cinematic excellence. Now streaming in Ultra HD with NitroFlix Premium.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                lineHeight = 26.sp,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { onPlayClicked(item) },
                    modifier = Modifier.width(160.dp).height(48.dp),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(6.dp)),
                    colors = ButtonDefaults.colors(
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    ),
                    scale = ButtonDefaults.scale(focusedScale = 1.1f),
                    glow = ButtonDefaults.glow(focusedGlow = Glow(Color(0xFFFF4500).copy(alpha = 0.3f), 12.dp))
                ) {
                    Text("▶  Play", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
                OutlinedButton(
                    onClick = { onMoreInfoClicked(item) },
                    modifier = Modifier.width(160.dp).height(48.dp),
                    shape = ButtonDefaults.shape(shape = RoundedCornerShape(6.dp)),
                    colors = ButtonDefaults.colors(
                        contentColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedContentColor = Color.Black
                    ),
                    scale = ButtonDefaults.scale(focusedScale = 1.1f),
                    glow = ButtonDefaults.glow(focusedGlow = Glow(Color.White.copy(alpha = 0.3f), 12.dp))
                ) {
                    Text("ⓘ  More Info", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun StreamRow(
    title: String,
    streams: List<StreamItem>,
    onStreamSelected: (StreamItem) -> Unit,
    isPosterStyle: Boolean = false
) {
    Column(
        modifier = Modifier.padding(top = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 56.dp)
        )

        TvLazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 56.dp)
        ) {
            if (streams.isEmpty()) {
                val placeholders = if (isPosterStyle) {
                    listOf(
                        "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=2070&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1594909122845-11baa439b7bf?q=80&w=2070&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1485846234645-a62644f84728?q=80&w=2059&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1925&auto=format&fit=crop"
                    )
                } else {
                    listOf(
                        "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=2070&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?q=80&w=2056&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1887&auto=format&fit=crop",
                        "https://images.unsplash.com/photo-1542204172-3c1f11c56f41?q=80&w=1974&auto=format&fit=crop"
                    )
                }
                itemsIndexed(placeholders) { index, url ->
                    MovieCard(
                        StreamItem("Movie ${index + 1}", "Featured", url, "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"),
                        { onStreamSelected(it) },
                        isPosterStyle
                    )
                }
            } else {
                items(streams) { stream ->
                    MovieCard(stream, onStreamSelected, isPosterStyle)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MovieCard(stream: StreamItem, onClick: (StreamItem) -> Unit, isPosterStyle: Boolean) {
    val cardWidth = if (isPosterStyle) 180.dp else 240.dp
    val cardHeight = if (isPosterStyle) 270.dp else 140.dp

    Card(
        onClick = { onClick(stream) },
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight),
        shape = CardDefaults.shape(shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(
            containerColor = Color(0xFF1A1A1A),
            focusedContainerColor = Color.White.copy(alpha = 0.15f)
        ),
        scale = CardDefaults.scale(focusedScale = 1.12f),
        glow = CardDefaults.glow(focusedGlow = Glow(Color(0xFFFF4500).copy(alpha = 0.3f), 12.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = stream.posterUrl,
                contentDescription = stream.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                            startY = 200f
                        )
                    )
            )
            
            Text(
                text = stream.title,
                color = Color.White,
                fontSize = 14.sp,
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TopGradientHeader(onSettingsSelected: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent)
                )
            )
            .padding(start = 56.dp, top = 24.dp, end = 56.dp, bottom = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("NITRO", color = Color(0xFFFF4500), fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
            Text("FLIX", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-1).sp)
        }
        
        OutlinedButton(
            onClick = onSettingsSelected,
            modifier = Modifier.size(48.dp),
            shape = ButtonDefaults.shape(shape = RoundedCornerShape(50)),
            colors = ButtonDefaults.colors(
                contentColor = Color.White,
                focusedContainerColor = Color.White,
                focusedContentColor = Color.Black
            ),
            scale = ButtonDefaults.scale(focusedScale = 1.1f),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text("⚙", fontSize = 24.sp)
        }
    }
}
