package com.minerva.app.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary              = MinervaBlue,
    onPrimary            = Color.White,
    primaryContainer     = MinervaBlueLight,
    onPrimaryContainer   = Color.White,
    secondary            = MinervaBlueDark,
    onSecondary          = Color.White,
    background           = BackgroundLight,
    onBackground         = OnBackgroundLight,
    surface              = SurfaceLight,
    onSurface            = OnBackgroundLight,
    surfaceVariant       = SurfaceVariantLight,
    onSurfaceVariant     = OnSurfaceVariantLight,
    outline              = OutlineLight,
)

private val DarkColorScheme = darkColorScheme(
    primary              = MinervaBlueLight,
    onPrimary            = Color.White,
    primaryContainer     = MinervaBlue,
    onPrimaryContainer   = Color.White,
    secondary            = MinervaBlueLight,
    onSecondary          = Color.White,
    background           = BackgroundDark,
    onBackground         = OnBackgroundDark,
    surface              = SurfaceDark,
    onSurface            = OnBackgroundDark,
    surfaceVariant       = SurfaceVariantDark,
    onSurfaceVariant     = OnSurfaceVariantDark,
    outline              = OutlineDark,
)

@Composable
fun MinervaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
