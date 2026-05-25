package com.google.io.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val IoDarkColorScheme = darkColorScheme(
    primary = IoBlue,
    onPrimary = Color.Black,
    secondary = IoGreen,
    onSecondary = Color.Black,
    tertiary = IoYellow,
    onTertiary = Color.Black,
    error = IoRed,
    onError = Color.Black,
    background = IoDarkBackground,
    onBackground = IoOnBackground,
    surface = IoDarkSurface,
    onSurface = IoOnSurface,
    surfaceVariant = IoDarkSurfaceVariant,
    onSurfaceVariant = IoOnSurfaceVariant,
    surfaceContainer = IoDarkSurfaceContainer
)

// We also define a light scheme just in case, though our app is primarily dark-themed!
private val IoLightColorScheme = lightColorScheme(
    primary = IoBlue,
    onPrimary = Color.White,
    secondary = IoGreen,
    onSecondary = Color.White,
    tertiary = IoYellow,
    onTertiary = Color.White
)

/**
 * TASK 1: Material You Dynamic Theming
 * This is the custom theme for our Google I/O app. 
 * We've updated it to support "Dynamic Colors" (Material You).
 *
 * @param darkTheme Whether the system is in dark mode.
 * @param dynamicColor Whether to use the device's wallpaper colors (Android 12+).
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoogleIoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Enabled by default for a personalized feel!
    content: @Composable () -> Unit
) {
    /**
     * How Dynamic Color works:
     * 1. The OS (Android 12+) extracts colors from the user's wallpaper.
     * 2. These colors are provided to the app via "dynamicColorScheme" functions.
     * 3. This makes the app feel "at home" on the user's device, matching their style.
     */
    val colorScheme = when {
        // We check if the device supports dynamic colors (Android 12 / API 31+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Fallback for older devices or if dynamic color is disabled
        darkTheme -> IoDarkColorScheme
        else -> IoLightColorScheme
    }

    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        // The Expressive motion scheme provides fluid, organic animations
        // that match the premium Material 3 design language.
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
