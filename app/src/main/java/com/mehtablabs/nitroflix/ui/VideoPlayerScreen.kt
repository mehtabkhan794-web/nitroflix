package com.mehtablabs.nitroflix.ui

import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(streamUrl: String, onPlaybackError: () -> Unit) {
    val context = LocalContext.current

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
                        Log.e("NitroFlixPlayer", "ExoPlayer Error Message: ${error.localizedMessage}")
                        
                        val message = when (error.errorCode) {
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED -> "Network Connection Failed - Check Emulator Internet"
                            PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> "Connection Timeout - Server too slow"
                            PlaybackException.ERROR_CODE_IO_FILE_NOT_FOUND -> "Stream URL not found (404)"
                            PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED -> "HTTP not allowed - needs HTTPS or Config fix"
                            else -> "Streaming Error: ${error.localizedMessage}"
                        }
                        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                        onPlaybackError()
                    }
                })
            }
    }

    LaunchedEffect(streamUrl) {
        val trimmedUrl = streamUrl.trim()
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(trimmedUrl)
        
        // Force HLS type if URL contains .m3u8 to help the player
        if (trimmedUrl.contains(".m3u8")) {
            mediaItemBuilder.setMimeType(MimeTypes.APPLICATION_M3U8)
        }
        
        val mediaItem = mediaItemBuilder.build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = true
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                requestFocus()
            }
        }
    )
}
