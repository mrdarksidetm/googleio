package com.google.io.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * TASK 2: Adaptive Main Navigation Scaffold
 * This Composable is like a "Smart Frame". 
 * It looks at the screen width and decides the best way to show the menu.
 * 
 * @param widthSizeClass The "bucket" our screen width falls into.
 * @param currentRoute The screen currently visible to the user.
 * @param navigationItems A list of all the main menu options.
 * @param onNavigate The function to call when a menu item is clicked.
 * @param content The actual screen content to show inside the frame.
 */
@Composable
fun AdaptiveScaffold(
    widthSizeClass: WindowWidthSizeClass,
    currentRoute: String?,
    navigationItems: List<NavigationItem>,
    onNavigate: (String) -> Unit,
    topBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    // 1. Breakpoint Logic: 
    // If the screen is "Compact" (Phone), we use a bottom bar.
    // If it's bigger (Medium or Expanded), we use a side rail!
    val isExpanded = widthSizeClass != WindowWidthSizeClass.Compact

    Row(modifier = Modifier.fillMaxSize()) {
        // 2. Navigation Rail (Side Menu)
        // We only show this if the screen is NOT compact (e.g., Tablet or Foldable).
        if (isExpanded) {
            NavigationRail {
                navigationItems.forEach { item ->
                    NavigationRailItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }

        // 3. Main Content Area
        Scaffold(
            topBar = topBar,
            bottomBar = {
                // 4. Bottom Navigation Bar
                // We only show this on small phone screens (Compact).
                if (!isExpanded) {
                    NavigationBar {
                        navigationItems.forEach { item ->
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.title) },
                                label = { Text(item.title) },
                                selected = currentRoute == item.route,
                                onClick = { onNavigate(item.route) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            // Here we show the actual screens (Home, Archives, etc.)
            // We use the padding provided by the Scaffold to avoid overlapping 
            // with the top and bottom bars.
            androidx.compose.foundation.layout.Box(modifier = Modifier.padding(innerPadding)) {
                content()
            }
        }
    }
}

/**
 * A simple data holder for our menu items.
 */
data class NavigationItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)
