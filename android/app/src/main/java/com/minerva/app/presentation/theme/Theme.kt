package com.minerva.app.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = MinervaBlue,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = MinervaBlueLight,
    secondary = MinervaBlueDark,
    surface = MinervaSurface,
    background = MinervaSurface
)

@Composable
fun MinervaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
