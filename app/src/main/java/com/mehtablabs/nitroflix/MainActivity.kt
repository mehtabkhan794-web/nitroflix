package com.mehtablabs.nitroflix

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.mehtablabs.nitroflix.network.RetrofitClient
import com.mehtablabs.nitroflix.network.StreamItem
import com.mehtablabs.nitroflix.ui.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            var streamList by remember { mutableStateOf<List<StreamItem>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                scope.launch {
                    val publicStreams = listOf(
                        // Featured / Hero
                        StreamItem(
                            title = "Tears of Steel",
                            category = "Trending Now",
                            posterUrl = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?q=80&w=1925&auto=format&fit=crop",
                            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
                        ),
                        // Stable Live TV Test Streams
                        StreamItem(
                            title = "Live TV Channel 1",
                            category = "Live TV",
                            posterUrl = "https://images.unsplash.com/photo-1595113316349-9fa4ee24f884?q=80&w=2072&auto=format&fit=crop",
                            streamUrl = "https://demo.unified-streaming.com/k8s/features/stable/video/tears-of-steel/tears-of-steel.ism/.m3u8"
                        ),
                        StreamItem(
                            title = "Live TV Channel 2",
                            category = "Live TV",
                            posterUrl = "https://images.unsplash.com/photo-1495020689067-958852a7765e?q=80&w=2070&auto=format&fit=crop",
                            streamUrl = "https://rtmp.vof.org.in:8443/hls/stream.m3u8"
                        ),
                        StreamItem(
                            title = "Live TV Channel 3",
                            category = "Live TV",
                            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1887&auto=format&fit=crop",
                            streamUrl = "https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8"
                        ),
                        // Popular Movies
                        StreamItem(
                            title = "Big Buck Bunny",
                            category = "Popular Movies",
                            posterUrl = "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?q=80&w=2070&auto=format&fit=crop",
                            streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"
                        ),
                        StreamItem(
                            title = "Elephant's Dream",
                            category = "Popular Movies",
                            posterUrl = "https://images.unsplash.com/photo-1598899134739-24c46f58b8c0?q=80&w=2056&auto=format&fit=crop",
                            streamUrl = "https://bitmovin-a.akamaihd.net/content/art-of-motion_720p_2mbps.m3u8"
                        ),
                        // Trending Now
                        StreamItem(
                            title = "Sintel",
                            category = "Trending Now",
                            posterUrl = "https://images.unsplash.com/photo-1478720568477-152d9b164e26?q=80&w=2070&auto=format&fit=crop",
                            streamUrl = "https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8"
                        ),
                        // Top Web Series
                        StreamItem(
                            title = "The Phantom Skate",
                            category = "Top Web Series",
                            posterUrl = "https://images.unsplash.com/photo-1542204172-3c1f11c56f41?q=80&w=1974&auto=format&fit=crop",
                            streamUrl = "https://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8"
                        ),
                        StreamItem(
                            title = "Underwater World",
                            category = "Top Web Series",
                            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?q=80&w=1887&auto=format&fit=crop",
                            streamUrl = "https://bitmovin-a.akamaihd.net/content/playhouse-vr/m3u8s/105560.m3u8"
                        )
                    )

                    try {
                        val myLiveJsonUrl = "https://gist.githubusercontent.com/mehtabkhan794-web/9e981d09358b0c7e384bf50c49c6a904/raw/7b2ebfb3a4039b733d9259ad5320f79a4f9a4550/playlist.json"
                        val response = RetrofitClient.instance.getCustomStreams(myLiveJsonUrl)
                        // Filter out potentially broken Live TV from remote source to prioritize stable ones
                        val filteredResponse = response.filter { it.category != "Live TV" }
                        streamList = publicStreams + filteredResponse
                        isLoading = false
                    } catch (e: Exception) {
                        streamList = publicStreams
                        isLoading = false
                        if (publicStreams.isEmpty()) {
                            errorMessage = "Network Error: ${e.localizedMessage}"
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (isLoading) {
                    SplashScreen(onAnimationFinished = {})
                } else if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red)
                } else {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        composable(Screen.Dashboard.route) {
                            MainDashboardScreen(streams = streamList, navController = navController)
                        }
                        composable(Screen.Movies.route) {
                            MainDashboardScreen(
                                streams = streamList.filter { it.category.contains("Movie", ignoreCase = true) || it.category == "Trending Now" },
                                navController = navController
                            )
                        }
                        composable(Screen.WebSeries.route) {
                            MainDashboardScreen(
                                streams = streamList.filter { it.category.contains("Series", ignoreCase = true) },
                                navController = navController
                            )
                        }
                        composable(Screen.Sports.route) {
                            MainDashboardScreen(
                                streams = streamList.filter { it.category.contains("Sports", ignoreCase = true) },
                                navController = navController
                            )
                        }
                        composable(Screen.LiveTV.route) {
                            MainDashboardScreen(
                                streams = streamList.filter { it.category == "Live TV" },
                                navController = navController
                            )
                        }
                        composable(Screen.Settings.route) {
                            SettingsScreen()
                        }
                        composable(
                            route = Screen.Player.route,
                            arguments = listOf(navArgument("streamUrl") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val streamUrl = backStackEntry.arguments?.getString("streamUrl") ?: ""
                            VideoPlayerScreen(
                                streamUrl = streamUrl,
                                onPlaybackError = {
                                    Toast.makeText(this@MainActivity, "Stream offline!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable(
                            route = Screen.Details.route,
                            arguments = listOf(navArgument("title") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: "Details"
                            DetailsScreen(title = title, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
