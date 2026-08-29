package com.impacttask.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Impact Task is a dark-first product by design (the monster mechanic reads
// best against a dark ground), but we still define a light scheme so the
// app doesn't break for users who force light mode at the OS level.
private val ImpactDarkColors = darkColorScheme(
    primary = Cyan,
    onPrimary = BgVoid,
    secondary = Blue,
    tertiary = Violet,
    background = BgVoid,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextMuted,
    outline = BorderStrong,
    error = Rose,
)

private val ImpactLightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    secondary = Cyan,
    tertiary = Violet,
    background = Color(0xFFF6F8FC),
    onBackground = Color(0xFF10141F),
    surface = Color.White,
    onSurface = Color(0xFF10141F),
    surfaceVariant = Color(0xFFEFF2F8),
    onSurfaceVariant = Color(0xFF57617C),
    outline = Color(0xFFD8DEEA),
    error = Rose,
)

@Composable
fun ImpactTaskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) ImpactDarkColors else ImpactLightColors
    MaterialTheme(
        colorScheme = colors,
        typography = ImpactTypography,
        content = content,
    )
}
