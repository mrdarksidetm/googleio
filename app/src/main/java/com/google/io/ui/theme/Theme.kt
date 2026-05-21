package com.google.io.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GoogleIoTheme(
    content: @Composable () -> Unit
) {
    MaterialExpressiveTheme(
        colorScheme = IoDarkColorScheme,
        motionScheme = MotionScheme.expressive(),
        content = content
    )
}
