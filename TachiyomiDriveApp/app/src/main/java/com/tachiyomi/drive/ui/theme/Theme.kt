package com.tachiyomi.drive.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TachiyomiBg = Color(0xFF121212)
val TachiyomiSurface = Color(0xFF1E1E1E)
val TachiyomiSurfaceVariant = Color(0xFF2A2A2A)
val TachiyomiAccent = Color(0xFF00B0FF)
val TachiyomiTextPrimary = Color(0xFFEEEEEE)
val TachiyomiTextSecondary = Color(0xFF9E9E9E)

private val DarkColorScheme = darkColorScheme(
    primary = TachiyomiAccent,
    background = TachiyomiBg,
    surface = TachiyomiSurface,
    surfaceVariant = TachiyomiSurfaceVariant,
    onPrimary = Color.Black,
    onBackground = TachiyomiTextPrimary,
    onSurface = TachiyomiTextPrimary,
    onSurfaceVariant = TachiyomiTextSecondary
)

@Composable
fun TachiyomiTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
