package com.mehtablabs.nitroflix.ui

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.PlayerView
import androidx.tv.material3.*
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@OptIn(UnstableApi::class, ExperimentalTvMaterial3Api::class)
@Composable
fun VideoPlayerScreen(streamUrl: String, onPlaybackError: () -> Unit) {
    val context = LocalContext.current
    var isControllerVisible by remember { mutableStateOf(true) }
    var isPlaying by remember { mutableStateOf(true) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    
    val playPauseFocusRequester = remember { FocusRequester() }
    val rootFocusRequester = remember { FocusRequester() }

    // Set aggressive timeouts (20 seconds) to handle slow connections/emulator issues
    val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent(Util.getUserAgent(context, "NitroFlix"))
        .setConnectTimeoutMs(20000)
        .setReadTimeoutMs(20000)
        .setAllowCrossProtocolRedirects(true)

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .setMediaSourceFactory(DefaultMediaSourceFactory(httpDataSourceFactory))
            .build().apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("NitroFlixPlayer", "ExoPlayer Error Code: ${error.errorCode}")
                        val message = "Streaming Error: ${error.localizedMessage}"
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        onPlaybackError()
                    }
                    override fun onIsPlayingChanged(playing: Boolean) {
                        isPlaying = playing
                    }
                    override fun onPlaybackStateChanged(state: Int) {
                        if (state == Player.STATE_READY) {
                            totalDuration = duration
                        }
                    }
                })
            }
    }

    // Progress update loop
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            if (totalDuration == 0L && exoPlayer.duration > 0) {
                totalDuration = exoPlayer.duration
            }
            delay(1000)
        }
    }

    // Auto-hide controller
    LaunchedEffect(isControllerVisible) {
        if (isControllerVisible) {
            delay(4000)
            isControllerVisible = false
        }
    }

    // Focus management: when controller is shown, focus the play/pause button
    LaunchedEffect(isControllerVisible) {
        if (isControllerVisible) {
            playPauseFocusRequester.requestFocus()
        } else {
            rootFocusRequester.requestFocus()
        }
    }

    fun showController() {
        isControllerVisible = true
    }

    LaunchedEffect(streamUrl) {
        val trimmedUrl = streamUrl.trim()
        val mediaItemBuilder = MediaItem.Builder().setUri(trimmedUrl)
        if (trimmedUrl.contains(".m3u8")) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        exoPlayer.setMediaItem(mediaItemBuilder.build())
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(rootFocusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    showController()
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter -> {
                            if (!isControllerVisible) {
                                if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                                return@onKeyEvent true
                            }
                        }
                        Key.DirectionLeft -> {
                            exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 10000))
                            return@onKeyEvent true
                        }
                        Key.DirectionRight -> {
                            exoPlayer.seekTo(minOf(exoPlayer.duration, exoPlayer.currentPosition + 10000))
                            return@onKeyEvent true
                        }
                    }
                }
                false
            }
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        // Custom Netflix-style TV Overlay
        AnimatedVisibility(
            visible = isControllerVisible,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            ) {
                // Top Header (Optional: Title could go here)
                Text(
                    text = "NitroFlix Original",
                    color = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.TopStart).padding(48.dp),
                    style = MaterialTheme.typography.labelMedium
                )

                // Center Controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(48.dp)
                ) {
                    // Rewind -10s
                    IconButton(
                        onClick = { 
                            exoPlayer.seekTo(maxOf(0, exoPlayer.currentPosition - 10000))
                            showController()
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Text("⏪", fontSize = 32.sp)
                    }

                    // Play/Pause
                    IconButton(
                        onClick = { 
                            if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                            showController()
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .focusRequester(playPauseFocusRequester),
                        scale = IconButtonDefaults.scale(focusedScale = 1.2f),
                        glow = IconButtonDefaults.glow(focusedGlow = Glow(Color.White.copy(alpha = 0.2f), 20.dp)),
                        shape = IconButtonDefaults.shape(CircleShape)
                    ) {
                        Text(if (isPlaying) "⏸" else "▶", fontSize = 40.sp, color = Color.White)
                    }

                    // Forward +10s
                    IconButton(
                        onClick = { 
                            exoPlayer.seekTo(minOf(exoPlayer.duration, exoPlayer.currentPosition + 10000))
                            showController()
                        },
                        modifier = Modifier.size(64.dp)
                    ) {
                        Text("⏩", fontSize = 32.sp)
                    }
                }

                // Bottom Seekbar Area
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 56.dp, vertical = 48.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Progress Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(3.dp))
                    ) {
                        val progress = if (totalDuration > 0) currentPosition.toFloat() / totalDuration else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .background(Color(0xFFFF4500), RoundedCornerShape(3.dp))
                        )
                    }

                    // Time Stamps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            formatTime(currentPosition),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            formatTime(totalDuration),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    
    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
