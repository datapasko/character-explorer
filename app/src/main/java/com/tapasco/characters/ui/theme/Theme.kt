package com.tapasco.characters.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = InterdimensionalGreen,
    onPrimary = OnGreen,
    primaryContainer = Color(0xFF3A570B),
    onPrimaryContainer = Color(0xFFD4FFA0),
    secondary = InterdimensionalYellow,
    onSecondary = OnYellow,
    secondaryContainer = Color(0xFF4C4700),
    onSecondaryContainer = Color(0xFFFFF58A),
    tertiary = InterdimensionalMagenta,
    onTertiary = OnMagenta,
    tertiaryContainer = Color(0xFF6C0052),
    onTertiaryContainer = Color(0xFFFFD8ED),
    background = InterdimensionalNeutral,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC8C7CD),
    outline = DarkOutline,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val LightColorScheme = lightColorScheme(
    primary = InterdimensionalGreen,
    onPrimary = OnGreen,
    primaryContainer = Color(0xFFD5FFA1),
    onPrimaryContainer = Color(0xFF1A2E00),
    secondary = InterdimensionalYellow,
    onSecondary = OnYellow,
    secondaryContainer = Color(0xFFFFF7A3),
    onSecondaryContainer = Color(0xFF242200),
    tertiary = InterdimensionalMagenta,
    onTertiary = OnMagenta,
    tertiaryContainer = Color(0xFFFFD8ED),
    onTertiaryContainer = Color(0xFF3D002E),
    background = LightBackground,
    onBackground = InterdimensionalNeutral,
    surface = LightSurface,
    onSurface = InterdimensionalNeutral,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF45483F),
    outline = LightOutline,
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
)

@Composable
fun CharactersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = CharactersTypography,
        content = content,
    )
}
