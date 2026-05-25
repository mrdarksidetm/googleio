package com.google.io.ui.components

import androidx.annotation.OptIn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 * This is our Video Player! 
 * It uses "Media3 ExoPlayer", which is Google's fancy tool for playing videos.
 */
@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    // 1. We "remember" the player so it doesn't restart every time the screen updates
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            // We give the player the video link
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            // We tell it to get ready to play
            prepare()
        }
    }

    // 2. This part is like a "cleanup crew". 
    // When we leave this screen, we MUST turn off the player to save battery!
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // 3. "AndroidView" is a special bridge. 
    // It lets us use "old" Android views (like the video player view) inside Compose.
    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp) // We make the video box 250 units tall
    )
}
