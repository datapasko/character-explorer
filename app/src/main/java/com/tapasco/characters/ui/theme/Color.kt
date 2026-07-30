package com.tapasco.characters.ui.theme

import androidx.compose.ui.graphics.Color

val InterdimensionalGreen = Color(0xFF97CE4C)
val InterdimensionalYellow = Color(0xFFF0E14A)
val InterdimensionalMagenta = Color(0xFFB91D8D)
val InterdimensionalNeutral = Color(0xFF1A1A1E)

val InterdimensionalRed = Color(0xFFE5484D)

internal fun characterStatusColor(status: String): Color = when {
    status.equals("alive", ignoreCase = true) -> InterdimensionalGreen
    status.equals("dead", ignoreCase = true) -> InterdimensionalRed
    else -> InterdimensionalYellow
}

internal val LightBackground = Color(0xFFF8F8F2)
internal val LightSurface = Color(0xFFFFFFFF)
internal val LightSurfaceVariant = Color(0xFFE5E7DC)
internal val LightOutline = Color(0xFF74766D)

internal val DarkSurface = Color(0xFF202024)
internal val DarkSurfaceVariant = Color(0xFF2B2B30)
internal val DarkOnSurface = Color(0xFFF1F1F4)
internal val DarkOutline = Color(0xFF92929A)

internal val OnGreen = Color(0xFF172109)
internal val OnYellow = Color(0xFF211F00)
internal val OnMagenta = Color(0xFFFFFFFF)
