package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GameColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = GameBgDark,
    primaryContainer = GameSurfaceBright,
    onPrimaryContainer = NeonCyan,
    secondary = NeonGold,
    onSecondary = GameBgDark,
    secondaryContainer = GameSurfaceElevated,
    onSecondaryContainer = NeonGold,
    tertiary = NeonCrimson,
    onTertiary = TextPrimary,
    background = GameBgDark,
    onBackground = TextPrimary,
    surface = GameSurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = GameSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = NeonCyanGlow
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = GameBgDark.toArgb()
                window.navigationBarColor = GameBgDark.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = false
                controller.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}
