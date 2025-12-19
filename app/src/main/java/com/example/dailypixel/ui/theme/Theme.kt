package com.example.dailypixel.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = RetroOrange,
    secondary = LeafGreen,
    tertiary = SoftGreen,
    background = DarkBrownShadow,
    surface = DarkGreenShadow,
    onPrimary = CardBackground,
    onSecondary = CardBackground,
    onTertiary = DarkText,
    onBackground = DefaultBackground,
    onSurface = CardBackground,
    outline = DarkText,
    surfaceVariant = DefaultBackground,
    primaryContainer = SoftGreen.copy(alpha = 0.2f)
)

private val LightColorScheme = lightColorScheme(
    primary = RetroOrange,
    secondary = LeafGreen,
    tertiary = SoftGreen,
    background = DefaultBackground,
    surface = CardBackground,
    onPrimary = CardBackground,
    onSecondary = CardBackground,
    onTertiary = DarkText,
    onBackground = DarkText,
    onSurface = DarkText,
    outline = DarkBrownShadow,
    surfaceVariant = CardBackground,
    surfaceTint = WarmHighlight,
    primaryContainer = SoftGreen.copy(alpha = 0.3f)
)

@Composable
fun DailyPixelTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Always use our custom pixel art theme colors
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}