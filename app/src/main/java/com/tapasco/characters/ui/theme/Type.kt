package com.tapasco.characters.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.tapasco.characters.R

val SpaceGrotesk = FontFamily(
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
)

val CharactersTypography = Typography(
    displayLarge = spaceGroteskStyle(57, 64),
    displayMedium = spaceGroteskStyle(45, 52),
    displaySmall = spaceGroteskStyle(36, 44),
    headlineLarge = spaceGroteskStyle(32, 40, FontWeight.Medium),
    headlineMedium = spaceGroteskStyle(28, 36, FontWeight.Medium),
    headlineSmall = spaceGroteskStyle(24, 32, FontWeight.Medium),
    titleLarge = spaceGroteskStyle(22, 28, FontWeight.Medium),
    titleMedium = spaceGroteskStyle(16, 24, FontWeight.Medium, 0.15f),
    titleSmall = spaceGroteskStyle(14, 20, FontWeight.Medium, 0.1f),
    bodyLarge = plusJakartaStyle(16, 24, letterSpacing = 0.5f),
    bodyMedium = plusJakartaStyle(14, 20, letterSpacing = 0.25f),
    bodySmall = plusJakartaStyle(12, 16, letterSpacing = 0.4f),
    labelLarge = spaceGroteskStyle(14, 20, FontWeight.Medium, 0.1f),
    labelMedium = spaceGroteskStyle(12, 16, FontWeight.Medium, 0.5f),
    labelSmall = spaceGroteskStyle(11, 16, FontWeight.Medium, 0.5f),
)

private fun spaceGroteskStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = SpaceGrotesk,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
)

private fun plusJakartaStyle(
    fontSize: Int,
    lineHeight: Int,
    fontWeight: FontWeight = FontWeight.Normal,
    letterSpacing: Float = 0f,
) = TextStyle(
    fontFamily = PlusJakartaSans,
    fontWeight = fontWeight,
    fontSize = fontSize.sp,
    lineHeight = lineHeight.sp,
    letterSpacing = letterSpacing.sp,
)
