package com.google.io.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * This screen lets us play web games right inside the app!
 * We use "WebView", which is like a tiny internet browser 
 * that lives inside our app.
 */
@Composable
fun GameScreen(url: String = "https://io.google/2026/games") {
    // Just like the video player, we use AndroidView to bring in a browser view
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                // 1. We tell the browser: "It's okay to run JavaScript!"
                // Most games need JavaScript to work.
                settings.javaScriptEnabled = true
                
                // 2. We use a "WebViewClient" so the browser doesn't try 
                // to open the link in Chrome or Safari. We want it to stay HERE!
                webViewClient = WebViewClient()
                
                // 3. We load the game link
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize() // Make the game take up the whole screen
    )
}
